package top.mylove7.live.living.interfaces.sku.vo;

/**
 * @Program: jiushi-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-13 10:14
 */
public class RedPacketReceiveVO {

    private Integer price;
    private String msg;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RedPacketReceiveVO{" +
                "price=" + price +
                ", mgs='" + msg + '\'' +
                '}';
    }
}
