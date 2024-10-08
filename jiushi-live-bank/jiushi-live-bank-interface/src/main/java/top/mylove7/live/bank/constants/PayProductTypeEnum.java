package top.mylove7.live.bank.constants;

/**
 * @Author jiushi
 *
 * @Description
 */
public enum PayProductTypeEnum {

    JIUSHI_COIN(0,"直播间充值-九十虚拟币产品");

    PayProductTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    Integer code;
    String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
