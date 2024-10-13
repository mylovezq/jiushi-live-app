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
public class ImOnlineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8966707365668168554L;
    private Long userId;
    private Long appId;
    private Long roomId;
    private Long loginTime;

}
