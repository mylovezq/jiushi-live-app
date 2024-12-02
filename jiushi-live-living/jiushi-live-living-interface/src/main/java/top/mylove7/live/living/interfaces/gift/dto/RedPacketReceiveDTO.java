package top.mylove7.live.living.interfaces.gift.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: jiushi
 *
 * @Create: 2024-08-12 18:06
 */
@Data
@NoArgsConstructor
public class RedPacketReceiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6017326621767275446L;
    private Long price;

    private String notifyMsg;

    public RedPacketReceiveDTO(Long price, String notifyMsg) {
        this.price = price;
        this.notifyMsg = notifyMsg;
    }


}
