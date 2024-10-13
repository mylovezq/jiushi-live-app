package top.mylove7.live.im.core.server.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class ImOfflineDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7435114723872010599L;
    private Long userId;
    private Long appId;
    private Long roomId;
    private Long loginTime;

}
