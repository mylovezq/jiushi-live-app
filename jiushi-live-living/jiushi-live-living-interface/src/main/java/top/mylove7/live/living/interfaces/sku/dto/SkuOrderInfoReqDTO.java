package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
public class SkuOrderInfoReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8947142795315398651L;

    private Long orderId;
    private Long userId;
    private Long roomId;
    private Long status;
    private List<Long> skuIdList;
}
