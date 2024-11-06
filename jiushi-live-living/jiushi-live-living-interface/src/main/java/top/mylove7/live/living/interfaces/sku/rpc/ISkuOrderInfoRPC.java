package top.mylove7.live.living.interfaces.sku.rpc;


import top.mylove7.live.living.interfaces.sku.dto.*;

public interface ISkuOrderInfoRPC {

    /**
     * 多直播间用户订单信息查询
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO querySkuOrderInfo(Long userId, Long roomId);

    /**
     * 新增订单
     * @param reqDTO
     * @return
     */
    boolean insertOne(SkuOrderInfoReqDTO reqDTO);

    /**
     * 预下单接口
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO reqDTO);

    /**
     * 支付
     * @param convert
     * @return
     */
    boolean payNow(PayNowReqDTO convert);
}
