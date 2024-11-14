package top.mylove7.live.living.provider.room.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.dto.PageWrapper;
import top.mylove7.live.im.core.server.interfaces.dto.ImOfflineDTO;
import top.mylove7.live.im.core.server.interfaces.dto.ImOnlineDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingPkRespDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.living.provider.room.service.ILivingRoomService;
import top.mylove7.live.living.provider.room.service.ILivingRoomTxService;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class LivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private ILivingRoomTxService livingRoomTxService;

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.queryUserIdByRoomId(livingRoomReqDTO);
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.list(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Long roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }


    @Override
    public Long startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomTxService.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.onlinePk(livingRoomReqDTO);
    }

    @Override
    public Long queryOnlinePkUserId(Long roomId) {
        return livingRoomService.queryOnlinePkUserId(roomId);
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.offlinePk(livingRoomReqDTO);
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        livingRoomService.userOnlineHandler(imOnlineDTO);
    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        livingRoomService.userOfflineHandler(imOfflineDTO);
    }
}
