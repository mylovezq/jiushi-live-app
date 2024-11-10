package top.mylove7.live.user.interfaces.bank.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class PayProductVO {

    /**
     * 当前余额
     */
    private Integer currentBalance;

    /**
     * 一系列的付费产品
     */
    private List<PayProductItemVO> payProductItemVOList;


}
