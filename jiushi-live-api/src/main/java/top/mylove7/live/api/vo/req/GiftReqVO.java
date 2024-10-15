package top.mylove7.live.api.vo.req;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class GiftReqVO {

    private int giftId;
    private Long roomId;
    private Long senderUserId;
    private Long receiverId;
    private int type;


}
