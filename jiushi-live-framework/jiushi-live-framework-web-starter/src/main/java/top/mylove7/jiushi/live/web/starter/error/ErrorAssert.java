package top.mylove7.jiushi.live.web.starter.error;


/**
 * @Author jiushi
 *
 * @Description
 */
public class ErrorAssert {


    /**
     * 判断参数不能为空
     *
     * @param obj
     * @param qiyuBaseError
     */
    public static void isNotNull(Object obj, QiyuBaseError qiyuBaseError) {
        if (obj == null) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * 判断字符串不能为空
     *
     * @param str
     * @param qiyuBaseError
     */
    public static void isNotBlank(String str, QiyuBaseError qiyuBaseError) {
        if (str == null || str.trim().length() == 0) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * flag == true
     *
     * @param flag
     * @param qiyuBaseError
     */
    public static void isTure(boolean flag, QiyuBaseError qiyuBaseError) {
        if (!flag) {
            throw new QiyuErrorException(qiyuBaseError);
        }
    }

    /**
     * flag == true
     *
     * @param flag
     * @param qiyuErrorException
     */
    public static void isTure(boolean flag, QiyuErrorException qiyuErrorException) {
        if (!flag) {
            throw qiyuErrorException;
        }
    }
}
