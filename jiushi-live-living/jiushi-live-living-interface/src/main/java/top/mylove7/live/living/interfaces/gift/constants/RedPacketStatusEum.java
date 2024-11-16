package top.mylove7.live.living.interfaces.gift.constants;

import lombok.Getter;

@Getter
public enum RedPacketStatusEum {

    IS_PREPARE(1, "待准备"),
    PREPARED(2, "已准备"),
    HAD_SEND(3, "已发送");

    RedPacketStatusEum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;


}