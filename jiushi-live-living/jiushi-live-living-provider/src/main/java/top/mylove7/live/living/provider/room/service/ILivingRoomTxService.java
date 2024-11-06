package top.mylove7.live.living.provider.room.service;


import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ILivingRoomTxService {

    /**
     * 关闭直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

}
