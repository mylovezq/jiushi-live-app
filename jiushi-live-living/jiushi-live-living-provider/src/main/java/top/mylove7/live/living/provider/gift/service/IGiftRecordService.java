package top.mylove7.live.living.provider.gift.service;

import top.mylove7.live.living.interfaces.gift.dto.GiftRecordDTO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IGiftRecordService {

    /**
     * 插入单个礼物信息
     *
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);

}
