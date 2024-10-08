package top.mylove7.live.living.provider.service;


import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;

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
