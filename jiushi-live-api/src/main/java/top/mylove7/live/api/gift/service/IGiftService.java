package top.mylove7.live.api.gift.service;

import top.mylove7.live.api.gift.vo.GiftReqVO;
import top.mylove7.live.api.gift.vo.GiftConfigVO;


import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IGiftService {

    /**
     * 展示礼物列表
     *
     * @return
     */
    List<GiftConfigVO> listGift();

    /**
     * 送礼
     *
     * @param giftReqVO
     * @return
     */
    boolean send(GiftReqVO giftReqVO);
}
