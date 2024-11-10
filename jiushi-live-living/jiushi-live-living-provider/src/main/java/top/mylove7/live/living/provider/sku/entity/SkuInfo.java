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
 * 商品sku信息表
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@TableName("t_sku_info")
@Data
public class SkuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

   
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

   
    private Long skuId;

   
    private Long skuPrice;

   
    private String skuCode;

   
    private String name;

   
    private String iconUrl;

   
    private String originalIconUrl;

   
    private String remark;

   
    private Byte status;

   
    private Long categoryId;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

}
