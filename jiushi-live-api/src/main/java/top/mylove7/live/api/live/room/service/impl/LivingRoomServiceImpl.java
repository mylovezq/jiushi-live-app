package top.mylove7.live.api.live.room.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.Assert;
import top.mylove7.live.api.live.room.service.ILivingRoomService;
import top.mylove7.live.common.interfaces.error.ApiErrorEnum;
import top.mylove7.live.api.live.room.vo.LivingRoomInitVO;
import top.mylove7.live.api.live.room.qo.LivingRoomReqQo;
import top.mylove7.live.api.live.room.qo.OnlinePkReqVO;
import top.mylove7.live.api.live.room.vo.LivingRoomPageRespVO;
import top.mylove7.live.api.live.room.vo.LivingRoomRespVO;
import org.apache.dubbo.config.annotation.DubboReference;

import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.dto.PageWrapper;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigReqDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigRespDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveVO;
import top.mylove7.live.living.interfaces.gift.rpc.IRedPacketConfigRPC;
import top.mylove7.live.living.interfaces.room.dto.LivingPkRespDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;

import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.error.BizErrorException;
import org.springframework.stereotype.Service;
import top.mylove7.live.user.user.dto.UserDTO;
import top.mylove7.live.user.user.interfaces.IUserRpc;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private IUserRpc userRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private IRedPacketConfigRPC redPacketConfigRPC;

    @Override
    public LivingRoomPageRespVO list(LivingRoomReqQo livingRoomReqQo) {
        PageWrapper<LivingRoomRespDTO> resultPage = livingRoomRpc.list(ConvertBeanUtils.convert(livingRoomReqQo,LivingRoomReqDTO.class));
        LivingRoomPageRespVO livingRoomPageRespVO = new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultPage.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHasNext(resultPage.isHasNext());
        return livingRoomPageRespVO;
    }

    @Override
    public Long startingLiving(Integer type) {
        Long userId = JiushiLoginRequestContext.getUserId();
        UserDTO userDTO = userRpc.getByUserId(userId);
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(userId);
        livingRoomReqDTO.setRoomName("主播-" + JiushiLoginRequestContext.getUserId() + "的直播间");
        livingRoomReqDTO.setCovertImg(userDTO.getAvatar());
        livingRoomReqDTO.setType(type);
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean onlinePk(OnlinePkReqVO onlinePkReqVO) {
        LivingRoomReqDTO reqDTO = BeanUtil.copyProperties(onlinePkReqVO, LivingRoomReqDTO.class);
        reqDTO.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
        reqDTO.setPkObjId(JiushiLoginRequestContext.getUserId());
        LivingPkRespDTO tryOnlineStatus = livingRoomRpc.onlinePk(reqDTO);
        ErrorAssert.isTure(tryOnlineStatus.isOnlineStatus(), new BizErrorException(-1,tryOnlineStatus.getMsg()));
        return true;
    }

    @Override
    public boolean closeLiving(Long roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(JiushiLoginRequestContext.getUserId());
        return livingRoomRpc.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Long roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, ApiErrorEnum.LIVING_ROOM_END);
        Map<Long,UserDTO> userDTOMap = userRpc.batchQueryUserInfo(Arrays.asList(respDTO.getAnchorId(),userId).stream().distinct().collect(Collectors.toList()));
        UserDTO anchor = userDTOMap.get(respDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        //给定一个默认的头像
        respVO.setAvatar(anchor.getAvatar());
        respVO.setDefaultBgImg(anchor.getAvatar());
        respVO.setWatcherAvatar(watcher.getAvatar());
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            //这种就是属于直播间已经不存在的情况了
            respVO.setRoomId(-1L);
        } else {
            respVO.setRoomId(respDTO.getId());
            respVO.setAnchorId(respDTO.getAnchorId());
            respVO.setAnchor(respDTO.getAnchorId().equals(userId));
        }
        if (Objects.equals(respVO.isAnchor(), true)) {
            RedPacketConfigRespDTO redPacketConfigRespDTO = redPacketConfigRPC.queryByAuthorId(userId);
            if (redPacketConfigRespDTO != null) {
                respVO.setRedPacketConfigCode(redPacketConfigRespDTO.getConfigCode());
            }
        }

        return respVO;
    }

    @Override
    public boolean prepareRedPacket(Long userId, Long roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        Assert.notNull(respDTO,"直播间不存在");
        Assert.notNull(userId,"用户不能为空");
        Assert.notNull(roomId,"房间号不能为空");

        return redPacketConfigRPC.prepareRedPacket(userId);
    }

    @Override
    public boolean startRedPacket(Long userId, String code) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByAuthorId(userId);
        Assert.notNull(respDTO,"用户不存在");
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setConfigCode(code);
        reqDTO.setRoomId(respDTO.getId());
        return redPacketConfigRPC.startRedPacket(reqDTO);
    }

    @Override
    public RedPacketReceiveVO receiveRedPacket(Long userId, String redPacketConfigCode) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setConfigCode(redPacketConfigCode);
        RedPacketReceiveDTO redPacketReceiveDTO = redPacketConfigRPC.receiveRedPacket(reqDTO);
        RedPacketReceiveVO redPacketReceiveVO = new RedPacketReceiveVO();
        if (redPacketReceiveDTO == null) {
            redPacketReceiveVO.setMsg("红包已派发完毕");
        } else {
            redPacketReceiveVO.setPrice(redPacketReceiveDTO.getPrice());
            redPacketReceiveVO.setMsg(redPacketReceiveDTO.getNotifyMsg());
        }
        return redPacketReceiveVO;
    }

    @Override
    public WebResponseVO initInfo() {
         livingRoomRpc.initInfo();
         return new WebResponseVO();
    }

}
