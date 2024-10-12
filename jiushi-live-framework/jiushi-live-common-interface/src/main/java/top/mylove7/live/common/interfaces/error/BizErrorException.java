package top.mylove7.live.common.interfaces.error;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class BizErrorException extends RuntimeException{

    private int errorCode;
    private String errorMsg;

    public BizErrorException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BizErrorException(BaseError baseError) {
        this.errorCode = baseError.getErrorCode();
        this.errorMsg = baseError.getErrorMsg();
    }
    public BizErrorException(String errorMsg) {
        this.errorCode = 500;
        this.errorMsg = errorMsg;
    }

}
