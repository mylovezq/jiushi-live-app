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
 * @Create: 2024-08-19 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayNowReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5673920967915546016L;

    private Long userId;
    private Long roomId;

}
