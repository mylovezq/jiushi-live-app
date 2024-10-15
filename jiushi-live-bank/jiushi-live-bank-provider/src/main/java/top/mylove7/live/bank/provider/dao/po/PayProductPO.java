package top.mylove7.live.bank.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

//送礼物服务（用户的账户需要有一定的余额）
//通过一个接口，返回可以购买的产品列表
//映射我们的每个虚拟商品
/**
 *
 * @Author jiushi
 *
 * @Description
 */
@TableName("t_pay_product")
@Data
public class PayProductPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private Integer price;
    private String extra;
    private Integer type;
    private Integer validStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
