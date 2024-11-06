package top.mylove7.live.living.interfaces.sku.qo;

import lombok.Data;

/**
 * @Author idea
 * @Date: Created in 10:58 2023/8/6
 * @Description
 */
@Data
public class GiftReqVO {

    private int giftId;
    private Integer roomId;
    private Long senderUserId;
    private Long receiverId;
    private int type;

}
