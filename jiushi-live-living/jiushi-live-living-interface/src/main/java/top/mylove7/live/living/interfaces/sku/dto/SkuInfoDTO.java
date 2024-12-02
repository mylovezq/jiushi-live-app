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
 * @Create: 2024-08-14 17:49
 */
@Data
public class SkuInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7111302331962061092L;

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

    LocalDateTime createTime;

    LocalDateTime updateTime;
}
