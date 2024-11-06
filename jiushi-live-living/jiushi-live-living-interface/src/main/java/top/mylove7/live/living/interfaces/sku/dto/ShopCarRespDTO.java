package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Program: qiyu-live-app
 *
 * @Description: 商品购物车数据展示
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-15 15:32
 */
@Data
public class ShopCarRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6213928122846364022L;

    private Long userId;
    private Long roomId;
    private Long totalPrice;
    private List<ShopCarItemRespDTO> shopCarItemRespDTOList;
}
