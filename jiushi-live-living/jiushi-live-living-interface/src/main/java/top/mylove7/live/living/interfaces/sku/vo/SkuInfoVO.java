package top.mylove7.live.living.interfaces.sku.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 09:56
 */

@Data
public class SkuInfoVO {
    private Long id;

    private Long skuId;

    private Long skuPrice;

    private String skuCode;

    private String name;

    private String iconUrl;

    private String originalIconUrl;

    private String remark;

    private Byte status;

    private Long categoryId;
}
