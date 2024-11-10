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
 * @Create: 2024-08-15 16:17
 */
@Data
public class ShopCarItemRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5754617334189955176L;

    private Integer count;
    private SkuInfoDTO skuInfoDTO;

    public ShopCarItemRespDTO(int count, SkuInfoDTO skuInfoDTO) {
        this.count = count;
        this.skuInfoDTO = skuInfoDTO;
    }
}
