package top.mylove7.live.user.interfaces.bank.constants;

import lombok.Getter;

/**
 * @Author jiushi
 *
 * @Description
 */
@Getter
public enum TradeTypeEnum {

    SEND_GIFT_TRADE(0,"送礼物交易"),
    LIVING_RECHARGE(1,"直播间充值");

    int code;
    String desc;

    TradeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
