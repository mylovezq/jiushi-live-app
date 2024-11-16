package top.mylove7.live.user.interfaces.bank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BalanceMqDto implements Serializable {

    private Long userId;

    private Long price;

    private Long tradeId;

    private String tradeType;

    public BalanceMqDto(Long userId, Long price, Long tradeId, String tradeType) {
        this.userId = userId;
        this.price = price;
        this.tradeId = tradeId;
        this.tradeType = tradeType;
    }
}
