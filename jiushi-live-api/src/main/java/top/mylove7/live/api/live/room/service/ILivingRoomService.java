package top.mylove7.live.api.live.room.service;

import top.mylove7.live.api.live.room.vo.LivingRoomInitVO;
import top.mylove7.live.api.live.room.qo.LivingRoomReqQo;
import top.mylove7.live.api.live.room.qo.OnlinePkReqVO;
import top.mylove7.live.api.live.room.vo.LivingRoomPageRespVO;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveVO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ILivingRoomService {

    /**
     * 直播间列表展示
     *
     * @param livingRoomReqQo
     * @return
     */
    LivingRoomPageRespVO list(LivingRoomReqQo livingRoomReqQo);

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

    boolean prepareRedPacket(Long userId, Long roomId);

    boolean startRedPacket(Long userId, String code);

    RedPacketReceiveVO receiveRedPacket(Long userId, String redPacketConfigCode);

    WebResponseVO initInfo();
}
