package top.mylove7.live.user.interfaces.bank.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BalanceMqDto implements Serializable {

    private Long userId;

    private Long price;

    private Long tradeId;

    private String tradeType;
}
