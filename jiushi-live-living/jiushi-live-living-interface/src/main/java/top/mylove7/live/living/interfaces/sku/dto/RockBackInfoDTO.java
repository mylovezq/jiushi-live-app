package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-16 17:21
 */
@Data
public class RockBackInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7085904237045846612L;

    private Long userId;
    private Long orderId;

    public RockBackInfoDTO(Long userId, Long orderId) {
        this.userId = userId;
        this.orderId = orderId;
    }
}
