package top.mylove7.live.living.interfaces.gift.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-12 16:47
 */
@Data
@NoArgsConstructor
public class RedPacketConfigRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 512945685142847172L;
    private Long anchorId;
    private Integer status;
    private Long totalPrice;
    private Integer totalCount;
    private String remark;
    private String configCode;


}
