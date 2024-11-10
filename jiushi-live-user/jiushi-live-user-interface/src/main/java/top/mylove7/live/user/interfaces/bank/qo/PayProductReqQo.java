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
     * 支付来源 (直播间，个人中心，聊天页面，第三方宣传页面，广告弹窗引导)
     * @see PaySourceEnum
     */
    @NotNull(message = "支付来源不能为空")
    private Integer paySource;

    /**
     * 支付渠道
     * @see PayChannelEnum
     */
    private Integer payChannel;

    private Long userId;


}
