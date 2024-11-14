package top.mylove7.live.living.interfaces.gift.rpc;

import top.mylove7.live.living.interfaces.gift.dto.GiftRecordDTO;


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
