package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftRecordDTO;


/**
 * 礼物接口
 *
 * @Author jiushi
 *
 * @Description
 */
public interface IGiftRecordRpc {

    /**
     * 插入单个礼物信息
     *
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);

}
