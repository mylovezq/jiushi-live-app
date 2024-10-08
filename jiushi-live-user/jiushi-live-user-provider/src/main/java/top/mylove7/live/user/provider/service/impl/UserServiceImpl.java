package top.mylove7.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;

import top.mylove7.jiushi.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.topic.UserProviderTopicNames;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.user.constants.CacheAsyncDeleteCode;
import top.mylove7.live.user.dto.UserCacheAsyncDeleteDTO;
import top.mylove7.live.user.dto.UserDTO;
import top.mylove7.live.user.provider.dao.mapper.IUserMapper;
import top.mylove7.live.user.provider.dao.po.UserPO;
import top.mylove7.live.user.provider.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class UserServiceImpl implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserPhoneServiceImpl.class);

    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;
    @Resource
    private ExecutorService executorService;

    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = cacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if (userDTO != null) {
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(key, userDTO, 30, TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        int updateStatus = userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        if (updateStatus > -1) {
            String key = cacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
            redisTemplate.delete(key);
            UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
            userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_INFO_DELETE.getCode());
            Map<String,Object> jsonParam = new HashMap<>();
            jsonParam.put("userId",userDTO.getUserId());
            userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));
            Message message = new Message();
            message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);

            message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
            //延迟一秒进行缓存的二次删除
            message.setDelayTimeLevel(1);
            try {
                mqProducer.send(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        userIdList = userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return Maps.newHashMap();
        }
        // redis
        List<String> keyList = new ArrayList<>();
        userIdList.forEach(userId -> {
            keyList.add(cacheKeyBuilder.buildUserInfoKey(userId));
        });
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(keyList).stream().filter(x -> x != null).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userDTOList) && userDTOList.size() == userIdList.size()) {
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
        }
        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdNotInCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x)).collect(Collectors.toList());
        // 多线程查询 替换了union all
        ConcurrentMap<Long, List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingByConcurrent(userId -> Long.valueOf(userId % 100)));
        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
        List<CompletableFuture> dbQueryResultCompletableFuture = new ArrayList<>();



        userIdMap.forEach((key, value) -> {
            CompletableFuture<Boolean> booleanCompletableFuture = CompletableFuture.supplyAsync(() -> {
                log.info("userMapper:{}", userMapper);
                List<UserPO> userPOS = userMapper.selectBatchIds(value);
                log.info("dbQueryResult:{}", userPOS);
                dbQueryResult.addAll(ConvertBeanUtils.convertList(userPOS, UserDTO.class));
                return true;
            },executorService);
            dbQueryResultCompletableFuture.add(booleanCompletableFuture);
        });

        CompletableFuture.allOf((dbQueryResultCompletableFuture.toArray(new CompletableFuture[0]))).join();

        if (!CollectionUtils.isEmpty(dbQueryResult)) {
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream().collect(Collectors.toMap(userDto -> cacheKeyBuilder.buildUserInfoKey(userDto.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            //对命令执行批量过期设置操作
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K) redisKey, createRandomTime(), TimeUnit.SECONDS);
                    }
                    return null;
                }
            });
            userDTOList.addAll(dbQueryResult);
        }
        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
    }

    /**
     * 创建随机的过期时间 用于redis设置key过期
     *
     * @return
     */
    private int createRandomTime() {
        int randomNumSecond = ThreadLocalRandom.current().nextInt(10000);
        return randomNumSecond + 30 * 60;
    }
}
