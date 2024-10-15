package top.mylove7.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author linhao
 * @Date created in 9:07 下午 2023/1/2
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
    private Date startTime;
    private Date updateTime;

}
