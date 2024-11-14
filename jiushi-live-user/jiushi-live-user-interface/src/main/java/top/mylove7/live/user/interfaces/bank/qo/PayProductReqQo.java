package top.mylove7.live.user.interfaces.bank.qo;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class PayProductReqQo {

    /**
     * 产品id
     */

    @NotNull(message = "产品id不能为空")
    private Integer productId;

    /**
     * 交易类型
     */
    @NotNull(message = "支付来源不能为空")
    private String tradeType;

    private Long userId;


}
