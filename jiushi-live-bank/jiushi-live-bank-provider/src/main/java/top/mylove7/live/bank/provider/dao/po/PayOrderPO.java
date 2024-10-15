package top.mylove7.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Integer source;
    private Integer payChannel;
    private Integer status;
    private Date payTime;
    private Date createTime;
    private Date updateTime;

}
