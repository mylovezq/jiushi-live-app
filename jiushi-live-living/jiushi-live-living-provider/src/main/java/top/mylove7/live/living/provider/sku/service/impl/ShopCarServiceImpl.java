package top.mylove7.live.living.provider.sku.service.impl;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarItemRespDTO;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarRespDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuInfoDTO;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;
import top.mylove7.live.living.provider.sku.service.IShopCarService;
import top.mylove7.live.living.provider.sku.service.ISkuInfoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 14:47
 */
@Service
public class ShopCarServiceImpl implements IShopCarService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder keyBuilder;
    @Resource
    private ISkuInfoService skuInfoService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO) {
        String cacheKey = keyBuilder.buildShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(cacheKey, ScanOptions.scanOptions().match("*").build());
        Map<Long, Integer> shopCarImteCountMap = new HashMap<>();
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            Long skuId = Long.valueOf((String) entry.getKey());
            Integer count = (Integer) entry.getValue();
            shopCarImteCountMap.put(skuId, count);
        }
        List<SkuInfo> skuInfoList = !shopCarImteCountMap.isEmpty() ? skuInfoService.queryBySkuIds(shopCarImteCountMap.keySet().stream().toList()) : new ArrayList<>();
        List<SkuInfoDTO> skuInfoDTOList = ConvertBeanUtils.convertList(skuInfoList, SkuInfoDTO.class);
        List<ShopCarItemRespDTO> shopCarItemRespDTOList = new ArrayList<>();
        Long totalPrice = 0L;
        for (SkuInfoDTO skuInfoDTO : skuInfoDTOList) {
            totalPrice += skuInfoDTO.getSkuPrice();
            shopCarItemRespDTOList.add(new ShopCarItemRespDTO(shopCarImteCountMap.get(skuInfoDTO.getSkuId()), skuInfoDTO));
        }
        ShopCarRespDTO shopCarRespDTO = new ShopCarRespDTO();
        shopCarRespDTO.setUserId(reqDTO.getUserId());
        shopCarRespDTO.setRoomId(reqDTO.getRoomId());
        shopCarRespDTO.setTotalPrice(totalPrice);
        shopCarRespDTO.setShopCarItemRespDTOList(shopCarItemRespDTOList);
        return shopCarRespDTO;
    }

    @Override
    public boolean addCar(ShopCarReqDTO reqDTO) {
        String cacheKey = keyBuilder.buildShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        //一个用户多个商品
        //读取所有商品的数据
        //每个商品都有数量（目前的业务场景中，没有体现)
        redisTemplate.opsForHash().put(cacheKey, String.valueOf(reqDTO.getSkuId()), 1);
        return true;
    }

    @Override
    public boolean removeFromCar(ShopCarReqDTO reqDTO) {
        String cacheKey = keyBuilder.buildShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(reqDTO.getSkuId()));
        return true;
    }

    @Override
    public boolean clearCar(ShopCarReqDTO reqDTO) {
        String cacheKey = keyBuilder.buildShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public boolean addCarItemNum(ShopCarReqDTO reqDTO) {
        String cacheKey = keyBuilder.buildShopCar(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.opsForHash().increment(cacheKey, reqDTO.getSkuId(), 1);
        return false;
    }
}
