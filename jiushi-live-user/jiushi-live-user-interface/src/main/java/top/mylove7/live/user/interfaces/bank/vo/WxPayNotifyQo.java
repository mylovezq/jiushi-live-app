package top.mylove7.live.user.interfaces.bank.vo;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class WxPayNotifyQo {

    private String orderId;
    private Long userId;
    private Integer bizCode;
}
