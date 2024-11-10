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
 * @Create: 2024-08-15 14:44
 */
@Data
public class ShopCarReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2735264285918350741L;
    private Long userId;
    private Long roomId;
    private Long skuId;
}
