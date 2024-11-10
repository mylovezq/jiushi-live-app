package top.mylove7.live.user.provider.bank.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 平台的虚拟币账户
 *
 * @Author jiushi
 *
 * @Description
 */
@TableName("t_currency_trade")
@Data
public class CurrencyTradePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long num;
    private Integer type;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
