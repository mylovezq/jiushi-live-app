package top.mylove7.live.api.bank;


import lombok.Data;
import top.mylove7.live.bank.constants.PayChannelEnum;
import top.mylove7.live.bank.constants.PaySourceEnum;

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
    private Integer productId;

    /**
     * 支付来源 (直播间，个人中心，聊天页面，第三方宣传页面，广告弹窗引导)
     * @see PaySourceEnum
     */
    private Integer paySource;

    /**
     * 支付渠道
     * @see PayChannelEnum
     */
    private Integer payChannel;


}
