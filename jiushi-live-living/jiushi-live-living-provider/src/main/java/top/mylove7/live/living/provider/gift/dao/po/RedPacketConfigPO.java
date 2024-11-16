package top.mylove7.live.living.provider.gift.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("t_red_packet_config")
@Data
public class RedPacketConfigPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long anchorId;
    private LocalDateTime startTime;
    private Integer totalGet;
    private Integer totalGetPrice;
    private Integer maxGetPrice;
    private Integer status;
    private Long totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
