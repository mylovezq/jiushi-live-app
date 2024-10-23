package top.mylove7.live.bank.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.mylove7.jiushi.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.provider.dao.maper.IPayProductMapper;
import top.mylove7.live.bank.provider.dao.po.PayProductPO;
import top.mylove7.live.bank.provider.service.IPayProductService;
import top.mylove7.live.bank.vo.PayProductItemVO;
import top.mylove7.live.bank.vo.PayProductVO;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiushi
 * @Description
 */
@Service
public class PayProductServiceImpl implements IPayProductService {

    @Resource
    private IPayProductMapper payProductMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder bankProviderCacheKeyBuilder;
    @Resource
    private MyCurrencyAccountServiceImpl myCurrencyAccountService;

    @Override
    public PayProductVO products(Integer type) {
        PayProductVO payProductVO = new PayProductVO();
        String cacheKey = bankProviderCacheKeyBuilder.buildPayProductCache(type);
        List  productsList = redisTemplate.opsForList().range(cacheKey, 0, 30);

        if (CollUtil.isNotEmpty(productsList)) {
            payProductVO.setPayProductItemVOList(productsList);
            return payProductVO;
        }


        List<PayProductPO> payProductPOS = payProductMapper.selectList(new LambdaQueryWrapper<PayProductPO>()
                .eq(PayProductPO::getType, type)
                .eq(PayProductPO::getValidStatus, CommonStatusEum.VALID_STATUS.getCode())
                .orderByDesc(PayProductPO::getPrice));
        if (CollectionUtils.isEmpty(payProductPOS)) {
            throw new BizErrorException("没有配置商品");
        }

        List<PayProductItemVO> payProductItemVOS = payProductPOS.parallelStream().map(product -> BeanUtil.copyProperties(product, PayProductItemVO.class)).toList();
        redisTemplate.opsForList().leftPushAll(cacheKey, payProductItemVOS);
        redisTemplate.expire(cacheKey, 60, TimeUnit.MINUTES);
        payProductVO.setPayProductItemVOList(payProductItemVOS);
        return payProductVO;

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
