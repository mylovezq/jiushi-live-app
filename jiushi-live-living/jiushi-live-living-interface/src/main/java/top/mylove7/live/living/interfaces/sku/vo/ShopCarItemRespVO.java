package top.mylove7.live.living.interfaces.sku.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.mylove7.live.living.interfaces.sku.dto.SkuInfoDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-15 16:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopCarItemRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4922676783085721925L;

    private Integer count;
    private SkuInfoDTO skuInfoDTO;
}
