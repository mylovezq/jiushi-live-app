package top.mylove7.live.api.live.room.qo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class LivingRoomReqQo {

    @NotNull(message = "类型不能为空")
    private Integer type;
    @NotNull(message = "页码不能为空")
    private Integer page;
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize;

}
