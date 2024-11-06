package top.mylove7.live.living.provider.sku.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 带货主播权限配置表
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@TableName("t_anchor_shop_info")
@Data
public class AnchorShopInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long anchorId;

    private Long skuId;

    private Byte status;


    Date createTime;


    Date updateTime;

}
