package top.mylove7.live.living.provider.sku.config;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuStockInfoRPC;
import top.mylove7.live.living.provider.sku.service.IAnchorShopInfoService;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 启动服务之后，每隔一段时间去同步直播间redis库存信息到DB中
 *
 * @Author idea
 * @Date: Created in 15:29 2023/7/25
 * @Description
 */
@Configuration
@Slf4j
public class RefreshSkuStockNumJob implements InitializingBean {

    @Resource
    private ISkuStockInfoRPC skuStockInfoRPC;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder keyBuilder;

    private final ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);

    @Override
    public void afterPropertiesSet() {
        //一秒钟刷新一次直播间列表数据
        schedulePool.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 15000, TimeUnit.MILLISECONDS);
    }

    /**
     * 在使用定时任务的时候需要注意，如果部署的时候，采用分布式节点进行部署，有可嫩多个节点同时执行定时任务，
     * 也就意味着下面的定时任务，将会有n多个进程同时执行，所以需要加分布式锁，确保只有加锁成功的任务，才能
     * 执行 refreshRedisToDB 方法
     */
    class RefreshCacheListJob implements Runnable {

        @Override
        public void run() {
            refreshRedisToDB();
        }
    }

    private void refreshRedisToDB() {
        String cacheKey = keyBuilder.buildSkuStockSyncLock();
        boolean lockStatus = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 15, TimeUnit.SECONDS));
        if (lockStatus) {
            List<Long> anchorIds = anchorShopInfoService.queryAllValidAnchorIds();
            for (Long anchorId : anchorIds) {
                skuStockInfoRPC.syncStockNumToMySql(anchorId);
            }
        }
    }
}
