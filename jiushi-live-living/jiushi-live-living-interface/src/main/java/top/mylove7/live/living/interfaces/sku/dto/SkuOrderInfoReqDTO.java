package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
public class SkuOrderInfoReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8947142795315398651L;

    private Long orderId;
    private Long userId;
    private Long roomId;
    private Integer status;
    private List<Long> skuIdList;
}
