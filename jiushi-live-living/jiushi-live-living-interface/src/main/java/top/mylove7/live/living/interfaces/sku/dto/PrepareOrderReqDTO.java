package top.mylove7.live.living.interfaces.sku.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-16 15:58
 */
@Data
public class PrepareOrderReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8532781733701185470L;

    private Long userId;
    private Long roomId;
}
