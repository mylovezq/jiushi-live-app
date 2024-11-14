package top.mylove7.live.living.interfaces.gift.rpc;

import top.mylove7.live.living.interfaces.gift.qo.GiftReqQo;


/**
 * 礼物接口
 *
 * @Author jiushi
 *
 * @Description
 */
public interface IGiftSendRpc {

    /**
     * 插入单个礼物信息
     *
     * @param giftReqQo
     */
    void sendGift(GiftReqQo giftReqQo);

}
