package top.mylove7.live.living.interfaces.sku.qo;

import lombok.Data;

/**
 * @Author idea
 * @Date: Created in 18:38 2023/7/23
 * @Description
 */
@Data
public class LivingRoomReqVO {

    private Integer type;
    private int page;
    private int pageSize;
    private Long roomId;
    private String redPacketConfigCode;

}
