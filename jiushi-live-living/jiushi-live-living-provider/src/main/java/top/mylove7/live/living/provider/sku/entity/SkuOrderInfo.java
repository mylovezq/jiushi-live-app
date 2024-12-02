package top.mylove7.live.living.provider.sku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 商品订单表
 * </p>
 *
 * @author jiushi
 * @since 2024-08-14
 */
@TableName("t_sku_order_info")
@Data
public class SkuOrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String skuIdList;

   
    private Long userId;

   
    private Long roomId;

   
    private Integer status;

   
    private String extra;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    
}
