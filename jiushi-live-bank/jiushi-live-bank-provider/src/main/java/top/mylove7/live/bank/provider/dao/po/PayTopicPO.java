package top.mylove7.live.bank.provider.dao.po;

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
@TableName("t_pay_topic")
@Data
public class PayTopicPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String topic;
    private Integer bizCode;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


}
