//package org.qiyu.live.api.service.impl;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import jakarta.annotation.Resource;
//import org.apache.dubbo.config.annotation.DubboReference;
//import service.mylove7.top.live.api.IBankService;
//import req.vo.mylove7.top.live.api.PayProductReqVO;
//import resp.vo.mylove7.top.live.api.PayProductItemVO;
//import resp.vo.mylove7.top.live.api.PayProductRespVO;
//import resp.vo.mylove7.top.live.api.PayProductVO;
//import org.qiyu.live.bank.constants.OrderStatusEnum;
//import org.qiyu.live.bank.dto.PayOrderDTO;
//import org.qiyu.live.bank.dto.PayProductDTO;
//import org.qiyu.live.bank.interfaces.IPayOrderRpc;
//import org.qiyu.live.bank.interfaces.IPayProductRpc;
//import org.qiyu.live.bank.interfaces.IQiyuCurrencyAccountRpc;
//import org.qiyu.live.bank.constants.PaySourceEnum;
//import context.top.mylove7.live.common.interfaces.JiushiLoginRequestContext;
//import error.top.mylove7.jiushi.live.web.starter.BizBaseErrorEnum;
//import error.top.mylove7.jiushi.live.web.starter.ErrorAssert;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
///**
// * @Author jiushi
// *
// * @Description
// */
//@Service
//public class BankServiceImpl implements IBankService {
//
//    @DubboReference
//    private IPayProductRpc payProductRpc;
//    @DubboReference
//    private IQiyuCurrencyAccountRpc qiyuCurrencyAccountRpc;
//    @DubboReference
//    private IPayOrderRpc payOrderRpc;
//    @Resource
//    private RestTemplate restTemplate;
//
//    @Override
//    public PayProductVO products(Integer type) {
//        List<PayProductDTO> payProductDTOS = payProductRpc.products(type);
//        PayProductVO payProductVO = new PayProductVO();
//        List<PayProductItemVO> itemList = new ArrayList<>();
//        for (PayProductDTO payProductDTO : payProductDTOS) {
//            PayProductItemVO itemVO = new PayProductItemVO();
//            itemVO.setName(payProductDTO.getName());
//            itemVO.setId(payProductDTO.getId());
//            itemVO.setCoinNum(JSON.parseObject(payProductDTO.getExtra()).getInteger("coin"));
//            itemList.add(itemVO);
//        }
//        payProductVO.setPayProductItemVOList(itemList);
//        payProductVO.setCurrentBalance(Optional.ofNullable(qiyuCurrencyAccountRpc.getBalance(JiushiLoginRequestContext.getUserId())).orElse(0));
//        return payProductVO;
//    }
//
//    @Override
//    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
//        //参数校验
//        ErrorAssert.isTure(payProductReqVO != null && payProductReqVO.getProductId() != null && payProductReqVO.getPaySource() != null, BizBaseErrorEnum.PARAM_ERROR);
//        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()), BizBaseErrorEnum.PARAM_ERROR);
//        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
//        ErrorAssert.isNotNull(payProductDTO, BizBaseErrorEnum.PARAM_ERROR);
//
//        //插入一条订单，待支付状态
//        PayOrderDTO payOrderDTO = new PayOrderDTO();
//        payOrderDTO.setProductId(payProductReqVO.getProductId());
//        payOrderDTO.setUserId(JiushiLoginRequestContext.getUserId());
//        payOrderDTO.setSource(payProductReqVO.getPaySource());
//        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
//        String orderId = payOrderRpc.insertOne(payOrderDTO);
//
//        //更新订单为支付中状态
//        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
//        PayProductRespVO payProductRespVO = new PayProductRespVO();
//        payProductRespVO.setOrderId(orderId);
//
//        //todo 远程http请求 resttemplate-》支付回调接口
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("orderId", orderId);
//        jsonObject.put("userId", JiushiLoginRequestContext.getUserId());
//        jsonObject.put("bizCode", 10001);
//        HashMap<String,String> paramMap = new HashMap<>();
//        paramMap.put("param",jsonObject.toJSONString());
//        ResponseEntity<String> resultEntity = restTemplate.postForEntity("http://localhost:8201/live/bank/payNotify/wxNotify?param={param}", null, String.class,paramMap);
//        System.out.println(resultEntity.getBody());
//        return payProductRespVO;
//    }
//}
