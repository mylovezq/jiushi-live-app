package top.mylove7.live.living.provider.sku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.mylove7.jiushi.live.framework.redis.starter.key.SkuProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoRespDTO;
import top.mylove7.live.living.provider.sku.entity.SkuOrderInfo;
import top.mylove7.live.living.provider.sku.mapper.SkuOrderInfoMapper;
import top.mylove7.live.living.provider.sku.service.ISkuOrderInfoService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品订单表 服务实现类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@Service
public class SkuOrderInfoServiceImpl extends ServiceImpl<SkuOrderInfoMapper, SkuOrderInfo> implements ISkuOrderInfoService {

    @Resource
    private SkuOrderInfoMapper skuOrderInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SkuProviderCacheKeyBuilder keyBuilder;

    @Override
    public SkuOrderInfoRespDTO querySkuOrderInfo(Long userId, Long roomId) {
        String cacheKey = keyBuilder.buildSkuOrderInfo(userId, roomId);

        Object object = redisTemplate.opsForValue().get(cacheKey);
        if (object != null) {
            return ConvertBeanUtils.convert(object, SkuOrderInfoRespDTO.class);
        }

        LambdaQueryWrapper<SkuOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuOrderInfo::getUserId, userId);
        queryWrapper.eq(SkuOrderInfo::getRoomId, roomId);
        queryWrapper.orderByDesc(SkuOrderInfo::getId);
        queryWrapper.last("limit 1");
        SkuOrderInfo skuOrderInfo = getOne(queryWrapper);
        if (skuOrderInfo == null) {
            return null;
        }
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfo, SkuOrderInfoRespDTO.class);
        redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO, 1, TimeUnit.HOURS);
        return skuOrderInfoRespDTO;
    }

    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Long orderId) {

        String cacheKey = keyBuilder.buildSkuOrderInfoByOrderId(orderId);

        Object object = redisTemplate.opsForValue().get(cacheKey);
        if (object != null) {
            return ConvertBeanUtils.convert(object, SkuOrderInfoRespDTO.class);
        }

        SkuOrderInfo skuOrderInfo = getById(orderId);
        if (skuOrderInfo == null) {
            return null;
        }
        SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfo, SkuOrderInfoRespDTO.class);
        redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO, 1, TimeUnit.HOURS);
        return skuOrderInfoRespDTO;
    }

    @Override
    public SkuOrderInfo insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        List<Long> skuIdList = skuOrderInfoReqDTO.getSkuIdList();
        String join = StringUtils.join(skuIdList, ",");
        SkuOrderInfo skuOrderInfo = ConvertBeanUtils.convert(skuOrderInfoReqDTO, SkuOrderInfo.class);
        skuOrderInfo.setSkuIdList(join);
        boolean isSuccess = this.save(skuOrderInfo);
        if (isSuccess) {
            String cacheKey = keyBuilder.buildSkuOrderInfo(skuOrderInfoReqDTO.getUserId(), skuOrderInfoReqDTO.getRoomId());
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfo, 1, TimeUnit.DAYS);
        }
        return skuOrderInfo;
    }

    @Override
    public boolean updateStatus(SkuOrderInfoReqDTO reqDTO) {
        SkuOrderInfo skuOrderInfo = new SkuOrderInfo();
        skuOrderInfo.setId(reqDTO.getOrderId());
        skuOrderInfo.setStatus(reqDTO.getStatus());
        return updateById(skuOrderInfo);
    }

    @Override
    public boolean clearCacheOrder(Long userId, Long roomId) {
        return Boolean.TRUE.equals(redisTemplate.delete(keyBuilder.buildSkuOrderInfo(userId, roomId)));
    }
}
