package top.mylove7.live.user.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class PayOrderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9209044050847451420L;

    private Long id;
    private String orderId;
    private Integer productId;
    private Integer bizCode;
    private Long userId;
    private Integer source;
    private Integer payChannel;
    private Integer status;
    private Date payTime;
}
