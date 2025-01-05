package top.mylove7.live.living.provider.room.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * 
 */
@Data
@TableName("t_my_data")
public class MyDataPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("my_data")
    private String myData;

}
