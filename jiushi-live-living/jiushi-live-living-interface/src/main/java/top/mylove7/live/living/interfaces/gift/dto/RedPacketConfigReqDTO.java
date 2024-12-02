package top.mylove7.live.living.interfaces.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-13 10:49
 */
@Data
public class RedPacketConfigReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2928794582321642709L;
    private Long roomId;
    private Long anchorId;
    private Long userId;
    private Integer status;
    private Long totalPrice;
    private Integer totalCount;
    private String remark;
    private String configCode;



}
