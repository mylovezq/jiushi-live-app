package top.mylove7.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import jakarta.annotation.Resource;
import top.mylove7.live.bank.provider.dao.maper.IPayTopicMapper;
import top.mylove7.live.bank.provider.dao.po.PayTopicPO;
import top.mylove7.live.bank.provider.service.IPayTopicService;
import org.springframework.stereotype.Service;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class PayTopicServiceImpl implements IPayTopicService {

    @Resource
    private IPayTopicMapper payTopicMapper;

    @Override
    public PayTopicPO getByCode(Integer code) {
        LambdaQueryWrapper<PayTopicPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayTopicPO::getBizCode,code);
        queryWrapper.eq(PayTopicPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return payTopicMapper.selectOne(queryWrapper);
    }
}
