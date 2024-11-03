package top.mylove7.live.bank.provider.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import top.mylove7.live.bank.constants.OrderStatusEnum;
import top.mylove7.live.bank.constants.PayProductTypeEnum;
import top.mylove7.live.bank.constants.PaySourceEnum;
import top.mylove7.live.bank.dto.PayOrderDTO;
import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.provider.dao.maper.IPayOrderMapper;
import top.mylove7.live.bank.provider.dao.po.PayOrderPO;
import top.mylove7.live.bank.provider.dao.po.PayTopicPO;
import top.mylove7.live.bank.provider.service.*;
import top.mylove7.live.bank.qo.PayProductReqQo;
import top.mylove7.live.bank.vo.PayProductRespVO;
import top.mylove7.live.bank.vo.WxPayNotifyQo;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import top.mylove7.live.common.interfaces.error.ErrorAssert;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class PayOrderServiceImpl implements IPayOrderService {


    @Resource
    private IPayOrderMapper payOrderMapper;
    @Resource
    private IPayProductService payProductService;
    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private MQProducer mqProducer;
    @Resource
    private IMyCurrencyAccountService myCurrencyAccountService;
    @Resource
    private ICurrencyTradeService currencyTradeService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(PayOrderPO::getOrderId, orderId);
        queryWrapper.last("limit 1");
        return payOrderMapper.selectOne(queryWrapper);
    }

    @Override
    public String insertOne(PayOrderPO payOrderPO) {
        String orderId = UUID.randomUUID().toString();
        payOrderPO.setOrderId(orderId);
        payOrderMapper.insert(payOrderPO);
        return orderId;
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setId(id);
        payOrderPO.setStatus(status);
        payOrderMapper.updateById(payOrderPO);
        return true;
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        PayOrderPO payOrderPO = new PayOrderPO();
        payOrderPO.setStatus(status);
        LambdaUpdateWrapper<PayOrderPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayOrderPO::getOrderId, orderId);
        payOrderMapper.update(payOrderPO, updateWrapper);
        return true;
    }

    @Override
    public boolean payNotify(WxPayNotifyQo wxPayNotifyQo) {
        PayOrderPO payOrderPO = this.queryByOrderId(wxPayNotifyQo.getOrderId());
        if (payOrderPO == null) {
            log.error("error payOrderPO, wxPayNotifyQo is {}", wxPayNotifyQo);
            return false;
        }
        PayTopicPO payTopicPO = payTopicService.getByCode(wxPayNotifyQo.getBizCode());
        if (payTopicPO == null || StrUtil.isEmpty(payTopicPO.getTopic())) {
            log.error("error payTopicPO, wxPayNotifyQo is {}", wxPayNotifyQo);
            return false;
        }
        this.payNotifyHandler(payOrderPO);
        //假设 支付成功后，要发送消息通知 -》 msg-provider
        //假设 支付成功后，要修改用户的vip经验值
        //发mq
        //中台服务，支付的对接方 10几种服务，pay-notify-topic
        Message message = new Message();
        message.setTopic(payTopicPO.getTopic());
        message.setBody(JSONUtil.toJsonStr(payOrderPO).getBytes(UTF_8));
        SendResult sendResult = null;
        try {
            sendResult = mqProducer.send(message);
            log.info("[payNotify] sendResult is {} ", sendResult);
        } catch (Exception e) {
            log.error("[payNotify] sendResult is {}, error is ", sendResult, e);
        }
        return true;
    }

    @Override
    public PayProductRespVO payProduct(PayProductReqQo payProductReqQo) {
        //参数校验
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqQo.getPaySource()), BizBaseErrorEnum.PARAM_ERROR);

        PayProductDTO payProductDTO = payProductService.getByProductId(payProductReqQo.getProductId());
        Assert.notNull(payProductDTO, "该商品不存在");

        //插入一条订单，待支付状态
        PayOrderPO payOrderPo = new PayOrderPO();
        payOrderPo.setProductId(payProductReqQo.getProductId());
        payOrderPo.setUserId(payProductReqQo.getUserId());
        payOrderPo.setSource(payProductReqQo.getPaySource());
        payOrderPo.setPayChannel(payProductReqQo.getPayChannel());
        String orderId = this.insertOne(payOrderPo);

        //更新订单为支付中状态
        this.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);

        //todo 远程http请求 resttemplate-》支付回调接口
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("orderId", orderId);
        jsonObject.set("userId", JiushiLoginRequestContext.getUserId());
        jsonObject.set("bizCode", 10001);
        HttpRequest httpRequest = HttpUtil.createPost("http://localhost/live/api/payNotify/wxNotify");
        httpRequest.body(jsonObject.toString());
        HttpResponse response = httpRequest.execute();
        log.info("请求的结果{}", response.body());
        return payProductRespVO;
    }

    /**
     * 增加用户余额
     *
     * @param payOrderPO
     */
    private void payNotifyHandler(PayOrderPO payOrderPO) {
        try {
            this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
            Integer productId = payOrderPO.getProductId();
            PayProductDTO payProductDTO = payProductService.getByProductId(productId);
            if (payProductDTO != null &&
                    PayProductTypeEnum.JIUSHI_COIN.getCode().equals(payProductDTO.getType())) {
                Long userId = payOrderPO.getUserId();
                JSONObject jsonObject = JSONUtil.parseObj(payProductDTO.getExtra());
                Integer num = jsonObject.getInt("coin");
                boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(payOrderPO.getId()+"", -1L, 24, TimeUnit.HOURS);
                if (!lockStatus) {
                    log.info("[payNotifyHandler] 该笔订单已经充值, orderId is {}", payOrderPO.getId());
                    return;
                }
                myCurrencyAccountService.incr(userId, num);
            }
        } catch (Exception e) {
            log.error("[payNotifyHandler] error is 充值失败 ", e);
            redisTemplate.delete(payOrderPO.getId());
            throw new BizErrorException("充值失败");
        }
    }

}
