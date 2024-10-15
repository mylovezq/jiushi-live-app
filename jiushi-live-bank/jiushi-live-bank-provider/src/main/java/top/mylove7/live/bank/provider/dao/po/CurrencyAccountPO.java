package top.mylove7.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 旗鱼平台的虚拟币账户
 *
 * @Author jiushi
 *
 * @Description
 */
@TableName("t_currency_account")
@Data
public class CurrencyAccountPO {

    @TableId(type = IdType.INPUT)
    private Long userId;
    private int currentBalance;
    private int totalCharged;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
