package top.mylove7.live.living.interfaces.sku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-19 15:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuPrepareOrderInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5101075046224519335L;

    private Integer totalPrice;
    private List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOList;
}
