package top.mylove7.live.living.provider.sku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.living.interfaces.sku.dto.RockBackInfoDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoRespDTO;
import top.mylove7.live.living.interfaces.sku.dto.UpdateStockNumDto;
import top.mylove7.live.living.provider.sku.entity.SkuOrderInfo;
import top.mylove7.live.living.provider.sku.entity.SkuStockInfo;
import top.mylove7.live.living.provider.sku.mapper.SkuStockInfoMapper;
import top.mylove7.live.living.provider.sku.service.ISkuOrderInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuStockInfoService;
import top.mylove7.live.user.interfaces.bank.constants.OrderStatusEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * sku库存表 服务实现类
 * </p>
 *
 * @author jiushi
 * @since 2024-08-14
 */
@Service
@Slf4j
public class SkuStockInfoServiceImpl extends ServiceImpl<SkuStockInfoMapper, SkuStockInfo> implements ISkuStockInfoService {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder keyBuilder;
    @Resource
    private ISkuOrderInfoService skuOrderInfoService;

    @Override
    public SkuStockInfo queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuStockInfo::getSkuId, skuId);
        queryWrapper.eq(SkuStockInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    public List<SkuStockInfo> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuStockInfo::getSkuId, skuIdList);
        queryWrapper.eq(SkuStockInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return list(queryWrapper);
    }

    @Override
    public void updateStockNumBySkuId(Long skuId, Integer stockNum) {
//        SkuStockInfo skuStockInfo = this.queryBySkuId(skuId);
//        int version = Math.toIntExact(skuStockInfo.getVersion());
//        boolean isSuccess = getBaseMapper().updateStockNumBySkuId(skuId, num, version) > 0;
//        return new UpdateStockNumDto(isSuccess, skuStockInfo.getStockNum() >= num);
        //or
        SkuStockInfo skuStockInfo = new SkuStockInfo();
        skuStockInfo.setSkuId(skuId);
        skuStockInfo.setStockNum(Long.valueOf(stockNum));
        LambdaQueryWrapper<SkuStockInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuStockInfo::getSkuId, skuId);
        update(skuStockInfo, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateStockNumDto decrStockNumBySkuIdDB(Long skuId, Integer num) {
        SkuStockInfo skuStockInfo = this.queryBySkuId(skuId);
        int version = Math.toIntExact(skuStockInfo.getVersion());
        boolean isSuccess = getBaseMapper().dcrStockNumBySkuId(skuId, num, version) > 0;
        //记录库存扣减记录，保证接口幂等
        //TODO
        return new UpdateStockNumDto(isSuccess, skuStockInfo.getStockNum() >= num);
    }

    private final String LUA_SCRIPT =
            "if (redis.call('exists',KEYS[1])) == 1 then " +
                    " local currentStock=redis.call('get',KEYS[1]) " +
                    "   if (tonumber(currentStock)>0 and tonumber(currentStock)-tonumber(ARGV[1])>=0) then " +
                    "       return redis.call('decrby',KEYS[1],tonumber(ARGV[1])) " +
                    "   else return -1 end " +
                    "else " +
                    "return -1 end";

    private final String BATCH_LUA_SCRIPT = "for i=1, ARGV[2] do    \n" +
            "   if (redis.call('exists', KEYS[i]))~= 1 then return -1 end\n" +
            "\tlocal currentStock=redis.call('get',KEYS[i])  \n" +
            "\tif (tonumber(currentStock)<=0 and tonumber(currentStock)-tonumber(ARGV[1])<0) then\n" +
            "       return -1\n" +
            "\tend\n" +
            "end  \n" +
            "\n" +
            "for  j=1,ARGV[2] do \n" +
            "\tredis.call('decrby',KEYS[j],tonumber(ARGV[1]))\n" +
            "end \n" +
            "return 1";

    @Override
    public boolean decrStockNumBySkuIdCache(List<Long> skuIdList, Integer num) {
        //直接使用redis命令操作的话，可能会有多元请求 用Lua方案去替代进行改良
        //根据skuId查询库存信息，可能会有多元请求 网络请求
        //判断：skU库存值>0，sku库存值-num>0 (其他线程也在这么操)
        //扣减 decrby 网络请求 导致超卖
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(BATCH_LUA_SCRIPT);
        script.setResultType(Long.class);
        List<String> skuIdCacheKeyLIst = new ArrayList<>();
        for (Long skuId : skuIdList) {
            String cacheKey = keyBuilder.buildSkuStock(skuId);
            skuIdCacheKeyLIst.add(cacheKey);
        }
        return redisTemplate.execute(script, skuIdCacheKeyLIst, num, skuIdCacheKeyLIst.size()) >= 0;
    }

    @Override
    public boolean decrStockNumBySkuIdCache(Long skuId, Integer num) {
        //直接使用redis命令操作的话，可能会有多元请求 用Lua方案去替代进行改良
        //根据skuId查询库存信息，可能会有多元请求 网络请求
        //判断：skU库存值>0，sku库存值-num>0 (其他线程也在这么操)
        //扣减 decrby 网络请求 导致超卖
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);
        String cacheKey = keyBuilder.buildSkuStock(skuId);
        return redisTemplate.execute(script, Collections.singletonList(cacheKey), num) >= 0;
    }

    @Override
    public boolean stockRollbackHandler(RockBackInfoDTO rockBackInfoDTO) {
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = skuOrderInfoService.queryByOrderId(rockBackInfoDTO.getOrderId());
        if (skuOrderInfoRespDTO == null || skuOrderInfoRespDTO.getStatus() == OrderStatusEnum.PAYED.getCode()) {
            return false;
        }
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setUserId(rockBackInfoDTO.getUserId());
        skuOrderInfoReqDTO.setStatus(OrderStatusEnum.IN_VALID.getCode());
        boolean update = skuOrderInfoService.lambdaUpdate()
                .set(SkuOrderInfo::getStatus, OrderStatusEnum.IN_VALID.getCode())
                .eq(SkuOrderInfo::getId, rockBackInfoDTO.getOrderId())
                .eq(SkuOrderInfo::getStatus, OrderStatusEnum.WAITING_PAY.getCode())
                .update();
        if (!update){
            log.info("订单不为待支付");
            return false;
        }
        //因为我们的直播带货场景比较特别，每件商品只能买一件
        List<Long> skuIdList = Arrays.stream(skuOrderInfoRespDTO.getSkuIdList().split(",")).toList().stream().map(Long::valueOf).toList();
        skuIdList.parallelStream().forEach(skuId -> {
            String cacheKey = keyBuilder.buildSkuStock(skuId);
            redisTemplate.opsForValue().increment(cacheKey, 1);
        });
        return true;
    }
}
