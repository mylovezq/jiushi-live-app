package top.mylove7.live.living.interfaces.sku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-19 15:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuPrepareOrderItemInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 124965709610826174L;
    
    private Integer count;
    private SkuInfoDTO skuInfoDTO;

}
