package top.mylove7.live.user.interfaces.bank.constants;

import lombok.Getter;

/**
 * @Author jiushi
 *
 * @Description
 */
@Getter
public enum PayProductTypeEnum {

    JIUSHI_COIN(0,"直播间充值-九十虚拟币产品");

    PayProductTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

}
