package top.mylove7.live.living.provider.sku.rpc;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.sku.dto.SkuStockInfoDTO;
import top.mylove7.live.living.interfaces.sku.dto.UpdateStockNumDto;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuStockInfoRPC;
import top.mylove7.live.living.provider.sku.entity.SkuStockInfo;
import top.mylove7.live.living.provider.sku.service.IAnchorShopInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuStockInfoService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 17:54
 */
@DubboService
@Slf4j
public class SkuStockInfoRPCImpl implements ISkuStockInfoRPC {

    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder keyBuilder;

    private static final int MAX_RETRY_TIMES = 3;

    @Override
    public boolean updateStockNumBySkuId(Long skuId, Integer num) {
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            UpdateStockNumDto updateStockNumDto = skuStockInfoService.decrStockNumBySkuIdDB(skuId, num);
            if (updateStockNumDto.isEmptyStock()) {
                return false;
            }
            if (updateStockNumDto.isSuccess()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dcrStockNumBySkuIdCache(Long skuId, Integer num) {
        return skuStockInfoService.decrStockNumBySkuIdCache(skuId, num);
    }

    @Override
    public SkuStockInfoDTO queryBySkuId(Long skuId) {
        return ConvertBeanUtils.convert(skuStockInfoService.queryBySkuId(skuId), SkuStockInfoDTO.class);
    }

    @Override
    public boolean prepareStockInfo(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAuthorId(anchorId);
        List< SkuStockInfo> stockInfoList = skuStockInfoService.queryBySkuIds(skuIdList);
        //通常来说一个主播带货的个数不多，可能也就几十个商品，所以使用f0r循环也是可以的，如果带货商品数量大也可以用 redisTemplate.opsForValue().multiSet();
//        for (SkuStockInfo skuStockInfo : stockInfoList) {
//            Long skuId = skuStockInfo.getSkuId();
//            String cacheKey = keyBuilder.buildSkuStock(skuId);
//            redisTemplate.opsForValue().set(cacheKey, skuStockInfo.getStockNum(), 1, TimeUnit.DAYS);
//        }
        Map<String, Long> saveCacheMap = stockInfoList.stream().collect(Collectors.toMap(stockInfo -> keyBuilder.buildSkuStock(stockInfo.getSkuId()), x -> x.getStockNum()));
        redisTemplate.opsForValue().multiSet(saveCacheMap);
        //对命令执行批量过期设置操作
        redisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (String redisKey : saveCacheMap.keySet()) {
                    operations.expire((K) redisKey, 1, TimeUnit.DAYS);
                }
                return null;
            }
        });
        return true;
    }

    @Override
    public int queryStockNum(Long skuId) {
        String cacheKey = keyBuilder.buildSkuStock(skuId);
        Object object = redisTemplate.opsForValue().get(cacheKey);
        return object != null ? (int) object : -1;
    }

    @Override
    public boolean syncStockNumToMySql(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAuthorId(anchorId);
        for (Long skuId : skuIdList) {
            int stockNum = this.queryStockNum(skuId);
            if (stockNum == -1) {
                continue;
            }
            //LOGGER.info("syncStockNumToMySql skuId: {}, stockNum: {}", skuId, stockNum);
            skuStockInfoService.updateStockNumBySkuId(skuId, stockNum);
        }
        return true;
    }
}
