package top.mylove7.live.living.interfaces.sku.qo;


import lombok.Data;

/**
 * @Author idea
 * @Date: Created in 20:17 2023/8/19
 * @Description
 */
@Data
public class PayProductReqVO {

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 支付来源 (直播间，个人中心，聊天页面，第三方宣传页面，广告弹窗引导)
     * @see org.qiyu.live.bank.constants.PaySourceEnum
     */
    private Integer paySource;

    /**
     * 支付渠道
     * @see org.qiyu.live.bank.constants.PayChannelEnum
     */
    private Integer payChannel;

}
