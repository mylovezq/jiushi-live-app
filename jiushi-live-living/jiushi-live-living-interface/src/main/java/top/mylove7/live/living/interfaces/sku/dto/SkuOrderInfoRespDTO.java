package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
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

    private Long status;

    private String extra;

    Date createTime;

    Date updateTime;

}
