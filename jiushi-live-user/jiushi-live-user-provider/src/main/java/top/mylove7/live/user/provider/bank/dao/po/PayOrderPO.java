package top.mylove7.live.user.provider.bank.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author jiushi
 *
 * @Description
 */
@TableName("t_pay_order")
@Data
public class PayOrderPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderId;
    private Integer productId;
    private Long userId;
    private String tradeType;
    private Long tradeId;
    private Integer status;
    private Long price;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
