package top.mylove7.live.api.live.room.vo;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class LivingRoomRespVO {

    private Long id;
    private String roomName;
    private Long anchorId;
    private Integer watchNum;
    private Integer goodNum;
    private Integer type;
    private String covertImg;

}
