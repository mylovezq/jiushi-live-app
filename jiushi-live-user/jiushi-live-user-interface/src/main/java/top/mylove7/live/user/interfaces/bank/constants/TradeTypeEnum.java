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
    LIVING_RECHARGE(1,"直播间充值"),
    LIVING_ROOM_SHOP(2,"直播间购物"),
    RED_PACKET_RECHARGE(2,"抢红包"),
    ;

    private final int code;
    private final String desc;

    TradeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
