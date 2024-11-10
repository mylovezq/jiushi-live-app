package top.mylove7.live.common.interfaces.dto;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class SendGiftMq {

    private Long userId;
    private Integer giftId;
    private Long price;
    private Long receiverId;
    private Long roomId;
    private String url;
    private String uuid;
    private Integer type;



}
