package top.mylove7.live.common.interfaces.constants;

/**
 * @Author jiushi
 *
 * @Description
 */
public enum ErrorAppIdEnum {

    QIYU_API_ERROR(101,"jiushi-live-api");

    ErrorAppIdEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
