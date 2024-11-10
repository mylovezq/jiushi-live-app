package top.mylove7.live.living.interfaces.gift.qo;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class GiftReqQo {

    private int giftId;
    private Long roomId;
    private Long senderUserId;
    private Long receiverId;
    private int type;


}
