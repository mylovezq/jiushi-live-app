package top.mylove7.live.user.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class AccountTradeReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7722121828825334678L;
    private long userId;
    private int num;

}
