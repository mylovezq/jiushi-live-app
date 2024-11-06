package top.mylove7.live.api.live.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class LivingRoomPageRespVO {

    private List<LivingRoomRespVO> list;
    private boolean hasNext;

}
