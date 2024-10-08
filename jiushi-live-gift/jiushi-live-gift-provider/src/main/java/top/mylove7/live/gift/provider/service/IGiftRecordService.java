package top.mylove7.live.gift.provider.service;

import org.qiyu.live.gift.dto.GiftRecordDTO;

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
