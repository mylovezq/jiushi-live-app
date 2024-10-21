package top.mylove7.live.api.gift.service;

import org.qiyu.live.gift.qo.GiftReqQo;
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
     * @param giftReqQo
     * @return
     */
    void send(GiftReqQo giftReqQo);
}
