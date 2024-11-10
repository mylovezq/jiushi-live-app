package top.mylove7.live.living.interfaces.sku.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author idea
 * @Date: Created in 15:16 2023/8/1
 * @Description
 */
@Data

public class GiftConfigVO {

    private Integer giftId;
    private Integer price;
    private String giftName;
    private Integer status;
    private String coverImgUrl;
    private String svgaUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
