package top.mylove7.live.api.live.qo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class OnlinePkReqVO {

    @NotNull(message = "房间id不能为空")
    private Long roomId;

}
