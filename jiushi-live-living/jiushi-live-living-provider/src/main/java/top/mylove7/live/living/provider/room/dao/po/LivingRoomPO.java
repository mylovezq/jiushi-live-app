package top.mylove7.live.living.provider.room.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * 
 */
@Data
@TableName("t_living_room")
public class LivingRoomPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long anchorId;
    private Integer type;
    private String roomName;
    private String covertImg;
    private Integer status;
    private Integer watchNum;
    private Integer goodNum;
    private LocalDateTime startTime;
    private LocalDateTime updateTime;

}
