package top.mylove7.live.living.provider.gift.dao.po;

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
@TableName("t_gift_config")
@Data
public class GiftConfigPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Integer giftId;
    private Long price;
    private String giftName;
    private Integer status;
    private String coverImgUrl;
    private String svgaUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
