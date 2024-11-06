package top.mylove7.live.living.interfaces.sku.constants;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-16 16:07
 */
public enum OrderStatusEnum {
    PREPARE_PAY(0, "待支付状态"),

    PAYED(1, "已支付状态"),

    END(2,"订单已关闭");

    int code;
    String desc;


    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    }
