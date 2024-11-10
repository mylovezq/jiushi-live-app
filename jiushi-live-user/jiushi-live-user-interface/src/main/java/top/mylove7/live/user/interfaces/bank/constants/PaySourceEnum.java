package top.mylove7.live.user.interfaces.bank.constants;

import lombok.Getter;

/**
 * 支付渠道类型
 *
 * @Author jiushi
 *
 * @Description
 */
@Getter
public enum PaySourceEnum {

    JIUSHI_LIVING_ROOM(1,"直播间内支付"),
    JIUSHI_USER_CENTER(2,"用户中心");

    PaySourceEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaySourceEnum find(int code) {
        for (PaySourceEnum value : PaySourceEnum.values()) {
            if(value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    private int code;
    private String desc;

}
