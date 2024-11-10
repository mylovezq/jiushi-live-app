package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-15 10:50
 */
@Data
public class SkuDetailInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -1858463450572581836L;
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
