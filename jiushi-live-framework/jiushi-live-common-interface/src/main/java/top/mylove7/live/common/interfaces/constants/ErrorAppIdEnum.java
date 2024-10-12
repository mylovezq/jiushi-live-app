package top.mylove7.live.common.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author jiushi
 *
 * @Description
 */
@Getter
public enum ErrorAppIdEnum {

    JIUSHI_API_ERROR(101,"jiushi-live-api");

    ErrorAppIdEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    final int code;
    final String msg;

}
