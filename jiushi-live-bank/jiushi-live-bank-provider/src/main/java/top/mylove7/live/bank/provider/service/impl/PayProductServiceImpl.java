package top.mylove7.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.provider.dao.maper.IPayProductMapper;
import top.mylove7.live.bank.provider.dao.po.PayProductPO;
import top.mylove7.live.bank.provider.service.IPayProductService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class PayProductServiceImpl implements IPayProductService {

    @Resource
    private IPayProductMapper payProductMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;

    @Override
    public List<PayProductDTO> products(Integer type) {
        String cacheKey = bankProviderCacheKeyBuilder.buildPayProductCache(type);
        List<PayProductDTO> cacheList = redisTemplate.opsForList().range(cacheKey, 0, 30).stream().map(x -> {
            return (PayProductDTO) x;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cacheList)) {
            //空值缓存
            if (cacheList.get(0).getId() == null) {
                return Collections.emptyList();
            }
            return cacheList;
        }
        LambdaQueryWrapper<PayProductPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayProductPO::getType, type);
        queryWrapper.eq(PayProductPO::getValidStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.orderByDesc(PayProductPO::getPrice);
        List<PayProductDTO> payProductDTOS = ConvertBeanUtils.convertList(payProductMapper.selectList(queryWrapper), PayProductDTO.class);
        if (CollectionUtils.isEmpty(payProductDTOS)) {
            redisTemplate.opsForList().leftPush(cacheKey, new PayProductDTO());
            redisTemplate.expire(cacheKey, 3, TimeUnit.MINUTES);
            return Collections.emptyList();
        }
        redisTemplate.opsForList().leftPushAll(cacheKey, payProductDTOS.toArray());
        redisTemplate.expire(cacheKey, 30, TimeUnit.MINUTES);
        return payProductDTOS;
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        //不用type参数，但是要多存一个redis对象
        String cacheKey = bankProviderCacheKeyBuilder.buildPayProductItemCache(productId);
        PayProductDTO payProductDTO = (PayProductDTO) redisTemplate.opsForValue().get(cacheKey);
        if (payProductDTO != null) {
            //空值缓存
            if (payProductDTO.getId() == null) {
                return null;
            }
            return payProductDTO;
        }
        PayProductPO payProductPO = payProductMapper.selectById(productId);
        if (payProductPO != null) {
            PayProductDTO resultItem = ConvertBeanUtils.convert(payProductPO, PayProductDTO.class);
            redisTemplate.opsForValue().set(cacheKey, resultItem, 30, TimeUnit.MINUTES);
            return resultItem;
        }
        //空值缓存
        redisTemplate.opsForValue().set(cacheKey, new PayProductDTO(), 5, TimeUnit.MINUTES);
        return null;
    }
}
