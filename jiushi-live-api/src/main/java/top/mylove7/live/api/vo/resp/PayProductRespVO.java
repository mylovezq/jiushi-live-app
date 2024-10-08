package top.mylove7.live.api.vo.resp;

/**
 * @Author jiushi
 *
 * @Description
 */
public class PayProductRespVO {

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    @Override
    public String toString() {
        return "PayProductRespVO{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}
