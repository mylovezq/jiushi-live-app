package top.mylove7.live.api.live.service;

import top.mylove7.live.api.live.vo.LivingRoomInitVO;
import top.mylove7.live.api.live.qo.LivingRoomReqVO;
import top.mylove7.live.api.live.qo.OnlinePkReqVO;
import top.mylove7.live.api.live.vo.LivingRoomPageRespVO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ILivingRoomService {

    /**
     * 直播间列表展示
     *
     * @param livingRoomReqVO
     * @return
     */
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);

    /**
     * 开启直播间
     *
     * @param type
     */
    Long startingLiving(Integer type);


    /**
     * 用户在pk直播间中，连上线请求
     *
     * @param onlinePkReqVO
     * @return
     */
    boolean onlinePk(OnlinePkReqVO onlinePkReqVO);

    /**
     * 关闭直播间
     *
     * @param roomId
     * @return
     */
    boolean closeLiving(Long roomId);

    /**
     * 根据用户id返回当前直播间相关信息
     *
     * @param userId
     * @param roomId
     * @return
     */
    LivingRoomInitVO anchorConfig(Long userId, Long roomId);

}
