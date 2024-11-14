package top.mylove7.live.user.provider.bank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import jakarta.annotation.Resource;
import top.mylove7.live.user.provider.bank.dao.maper.IPayTopicMapper;
import top.mylove7.live.user.provider.bank.dao.po.PayTopicPO;
import top.mylove7.live.user.provider.bank.service.IPayTopicService;
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
    public PayTopicPO getByCode(String code) {
        LambdaQueryWrapper<PayTopicPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayTopicPO::getBizCode,code);
        queryWrapper.eq(PayTopicPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return payTopicMapper.selectOne(queryWrapper);
    }
}
