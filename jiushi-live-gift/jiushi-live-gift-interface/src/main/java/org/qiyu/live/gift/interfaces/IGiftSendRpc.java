package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.qo.GiftReqQo;


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
