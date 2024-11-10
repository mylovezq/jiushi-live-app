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
 * 类目表
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@TableName("t_category_info")
@Data
public class CategoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;


    private Long level;


    private Long parentId;

   
    private String categoryName;

   
    private Integer status;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
