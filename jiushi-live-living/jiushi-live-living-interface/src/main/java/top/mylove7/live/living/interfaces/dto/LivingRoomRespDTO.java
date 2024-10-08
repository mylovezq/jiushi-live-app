package top.mylove7.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 直播间相关请求DTO
 *
 * @Author jiushi
 *
 * @Description
 */
@Data
public class LivingRoomRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4370402310595190339L;
    private Long id;
    private Long anchorId;
    private String roomName;
    private String covertImg;
    private Integer type;
    private Integer watchNum;
    private Integer goodNum;
    private Long pkObjId;


}
