package top.mylove7.live.living.interfaces.gift.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class GiftConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2285354775828848375L;

    private Integer giftId;
    private Long price;
    private String giftName;
    private Integer status;
    private String coverImgUrl;
    private String svgaUrl;
    private Date createTime;
    private Date updateTime;

}
