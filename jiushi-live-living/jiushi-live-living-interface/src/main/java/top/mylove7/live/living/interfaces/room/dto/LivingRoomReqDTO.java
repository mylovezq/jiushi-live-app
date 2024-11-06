package top.mylove7.live.living.interfaces.room.dto;

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
public class LivingRoomReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4370401310595190339L;
    private Integer id;
    private Long anchorId;
    private Long pkObjId;
    private String roomName;
    private Long roomId;
    private String covertImg;
    private Integer type;
    private Long appId;
    private int page;
    private int pageSize;

}
