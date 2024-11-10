package top.mylove7.live.living.interfaces.sku.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-15 16:40
 */
@Data
public class ShopCarRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3351853678580654635L;

    private Long userId;
    private Long roomId;
    private Long totalPrice;
    private List<ShopCarItemRespVO> shopCarItemRespVOList;
}
