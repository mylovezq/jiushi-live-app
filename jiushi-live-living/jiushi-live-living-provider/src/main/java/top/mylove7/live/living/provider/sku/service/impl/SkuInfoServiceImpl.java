package top.mylove7.live.living.provider.sku.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;
import top.mylove7.live.living.provider.sku.mapper.SkuInfoMapper;
import top.mylove7.live.living.provider.sku.service.ISkuInfoService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品sku信息表 服务实现类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements ISkuInfoService {

    @Resource
    private SkuInfoMapper skuInfoMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder keyBuilder;


    @Override
    public List<SkuInfo> queryBySkuIds(List<Long> idsList) {
        if (CollUtil.isEmpty(idsList)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfo::getSkuId, idsList);
        queryWrapper.eq(SkuInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return list(queryWrapper);
    }

    @Override
    public SkuInfo queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfo::getSkuId, skuId);
        queryWrapper.eq(SkuInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    public SkuInfo queryBySkuIdFromCache(Long skuId) {
        String cacheKey = keyBuilder.buildSkuDetail(skuId);
        Object object = redisTemplate.opsForValue().get(cacheKey);
        if (object != null) {
            SkuInfo skuInfo = (SkuInfo) object;
            //空值缓存
            if (skuInfo.getId() == null) {
                return null;
            }
            return skuInfo;
        }
        SkuInfo skuInfo = this.queryBySkuId(skuId);
        if (skuInfo != null) {
            redisTemplate.opsForValue().set(cacheKey, skuInfo, 1, TimeUnit.DAYS);
            return skuInfo;
        }
        //空值缓存
        redisTemplate.opsForValue().set(cacheKey, new SkuInfo(), 1, TimeUnit.DAYS);
        return null;
    }
}
