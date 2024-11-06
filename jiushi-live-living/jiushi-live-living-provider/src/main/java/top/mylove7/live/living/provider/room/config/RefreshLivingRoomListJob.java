package top.mylove7.live.living.provider.room.config;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.living.interfaces.room.constants.LivingRoomTypeEnum;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.provider.room.service.ILivingRoomService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 启动服务之后，每隔一段时间去同步下直播间信息到redis的数据
 *
 * @Author jiushi
 *
 * @Description
 */
@Configuration
@Slf4j
public class RefreshLivingRoomListJob implements InitializingBean {



    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;

    private ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        //一秒钟刷新一次直播间列表数据
        schedulePool.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 5000, TimeUnit.MILLISECONDS);
    }

    class RefreshCacheListJob implements Runnable {

        @Override
        public void run() {
            String cacheKey = cacheKeyBuilder.buildRefreshLivingRoomListLock();
            //这把锁等它自动过期
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 1, TimeUnit.SECONDS);
            if (lockStatus) {
                log.debug("[RefreshLivingRoomListJob] starting 加载db中记录的直播间进redis里");
                refreshDBToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDBToRedis(LivingRoomTypeEnum.PK_LIVING_ROOM.getCode());
                log.debug("[RefreshLivingRoomListJob] end 加载db中记录的直播间进redis里");
            }
        }
    }

    private void refreshDBToRedis(Integer type) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(type);
        List<LivingRoomRespDTO> resultList = livingRoomService.listAllLivingRoomFromDB(type);
        if(CollectionUtils.isEmpty(resultList)) {
            redisTemplate.delete(cacheKey);
            return;
        }
        String tempListName = cacheKey + "_temp";
        //按照查询出来的顺序，一个个地塞入到list集合中
        for (LivingRoomRespDTO livingRoomRespDTO : resultList) {
            redisTemplate.opsForList().rightPush(tempListName, livingRoomRespDTO);
        }
        //正在访问的list集合，del -> leftPush
        //直接修改重命名这个list，不要直接对原先的list进行修改，减少阻塞影响 cow
        redisTemplate.rename(tempListName, cacheKey);
        redisTemplate.delete(tempListName);
    }
}
