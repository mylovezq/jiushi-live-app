package top.mylove7.live.common.interfaces.error;

import lombok.Data;
import lombok.ToString;

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
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;

    }

    public BizErrorException(BaseError baseError) {
        super(baseError.getErrorMsg());
        this.errorCode = baseError.getErrorCode();
        this.errorMsg = baseError.getErrorMsg();
    }
    public BizErrorException(String errorMsg) {
        super(errorMsg);
        this.errorCode = 500;
        this.errorMsg = errorMsg;
    }

}
