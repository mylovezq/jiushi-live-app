package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-16 13:48
 */
@Data
public class SkuOrderInfoRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8080218092477992411L;

    private Long id;

    private String skuIdList;

    private Long userId;

    private Long roomId;

    private Integer status;

    private String extra;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
