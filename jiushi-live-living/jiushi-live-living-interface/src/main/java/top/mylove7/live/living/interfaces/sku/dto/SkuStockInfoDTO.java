package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 17:53
 */
@Data
public class SkuStockInfoDTO {
    private Long id;

    private Long skuId;

    private Long stockNum;

    private Byte status;

    private Long version;
}
