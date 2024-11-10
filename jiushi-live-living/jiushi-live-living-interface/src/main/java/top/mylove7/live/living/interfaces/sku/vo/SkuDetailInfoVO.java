package top.mylove7.live.living.interfaces.sku.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-15 10:42
 */
@Data
public class SkuDetailInfoVO {
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
