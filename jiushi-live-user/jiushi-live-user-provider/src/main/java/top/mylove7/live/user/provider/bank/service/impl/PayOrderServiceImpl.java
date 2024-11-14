package top.mylove7.live.user.provider.bank.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import top.mylove7.live.user.interfaces.bank.constants.OrderStatusEnum;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.interfaces.bank.dto.PayProductDTO;
import top.mylove7.live.user.interfaces.bank.qo.PayProductReqQo;
import top.mylove7.live.user.interfaces.bank.vo.PayProductRespVO;
import top.mylove7.live.user.interfaces.bank.vo.WxPayNotifyQo;
import top.mylove7.live.user.provider.bank.dao.maper.IPayOrderMapper;
import top.mylove7.live.user.provider.bank.dao.po.PayOrderPO;
import top.mylove7.live.user.provider.bank.service.IMyCurrencyAccountService;
import top.mylove7.live.user.provider.bank.service.IPayOrderService;
import top.mylove7.live.user.provider.bank.service.IPayProductService;
import top.mylove7.live.user.provider.bank.service.IPayTopicService;

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
    private IMyCurrencyAccountService myCurrencyAccountService;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(PayOrderPO::getOrderId, orderId);
        queryWrapper.last("limit 1");
        return payOrderMapper.selectOne(queryWrapper);
    }

    @Override
    public void insertOne(PayOrderPO payOrderPO) {

        payOrderMapper.insert(payOrderPO);

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
    @Transactional(rollbackFor = Exception.class)
    public boolean payNotify(WxPayNotifyQo wxPayNotifyQo) {

        //简单接收，正式环境要做验签
        PayOrderPO payOrderPO = this.queryByOrderId(wxPayNotifyQo.getOrderId());
        if (payOrderPO == null) {
            log.error("订单信息为空{}", wxPayNotifyQo);
            return false;
        }

        this.updateOrderStatus(payOrderPO.getOrderId(), OrderStatusEnum.PAYED.getCode());
        BalanceMqDto balanceMqDto = BeanUtil.copyProperties(payOrderPO, BalanceMqDto.class);
        myCurrencyAccountService.incr(balanceMqDto);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayProductRespVO payProduct(PayProductReqQo payProductReqQo) {
        //参数校验
        PayProductDTO payProductDTO = payProductService.getByProductId(payProductReqQo.getProductId());
        Assert.notNull(payProductDTO, "该商品不存在");

        //插入一条订单，待支付状态
        PayOrderPO payOrderPo = new PayOrderPO();
        payOrderPo.setProductId(payProductReqQo.getProductId());
        payOrderPo.setUserId(payProductReqQo.getUserId());
        payOrderPo.setTradeType(payProductReqQo.getTradeType());
        payOrderPo.setStatus(OrderStatusEnum.PAYING.getCode());
        payOrderPo.setOrderId(IdWorker.getId() + "");
        payOrderPo.setTradeId(IdWorker.getId());

        this.insertOne(payOrderPo);

        WxPayNotifyQo wxPayNotifyQo = new WxPayNotifyQo();
        wxPayNotifyQo.setOrderId(payOrderPo.getOrderId());

        //todo 远程http请求 resttemplate-》支付回调接口
        HttpRequest httpRequest = HttpUtil.createPost("http://localhost/live/api/payNotify/wxNotify");
        httpRequest.body(JSONUtil.toJsonStr(wxPayNotifyQo));
        HttpResponse response = httpRequest.execute();
        log.info("请求的结果{}", response.body());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(payOrderPo.getOrderId());
        return payProductRespVO;
    }


}
