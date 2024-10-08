package top.mylove7.live.api.service;

import top.mylove7.live.api.vo.req.GiftReqVO;
import top.mylove7.live.api.vo.resp.GiftConfigVO;


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
