package top.mylove7.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.msg.interfaces.ImRouterRpc;
import top.mylove7.live.msg.dto.ChatRoomMessageDTO;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.rpc.ILivingRoomRpc;
import top.mylove7.live.msg.provider.consumer.handler.MessageHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 * @Description
 */
@Component
@Slf4j
public class SingleMessageHandlerImpl implements MessageHandler {


    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    @Override
    public void onMsgReceive(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        int bizCode = imMsgBodyInTcpWsDto.getBizCode();
        log.info("im消息-msg-provider服务收到消息，消息内容：{}", imMsgBodyInTcpWsDto);
        //直播间的聊天消息
        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() == bizCode) {
            //一个人发送 n个人接收
            // 根据roomId，appId 去调用rpc方法，获取对应的直播间内的userId
            // 创建一个list的imMsgBody对象，
            ChatRoomMessageDTO chatRoomMessageDTO = JSON.parseObject(imMsgBodyInTcpWsDto.getData(), ChatRoomMessageDTO.class);
            log.info("直播室的im消息{}", chatRoomMessageDTO);

            LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
            reqDTO.setRoomId(chatRoomMessageDTO.getRoomId());
            reqDTO.setAppId(imMsgBodyInTcpWsDto.getAppId());
            List<Long> roodIds = Optional.ofNullable(livingRoomRpc.queryUserIdByRoomId(reqDTO)).orElse(new ArrayList<>());
            log.info("发送im消息的直播室{}", roodIds);
            //自己不用发
            List<ImMsgBodyInTcpWsDto> imMsgInTcpWsBodies
                    = roodIds
                    .parallelStream()
                    .filter(userId -> !Objects.equals(imMsgBodyInTcpWsDto.getFromUserId(), userId))
                    .map(toUserId -> {
                        ImMsgBodyInTcpWsDto respMsg = new ImMsgBodyInTcpWsDto();
                        respMsg.setToUserId(toUserId);
                        respMsg.setFromUserId(imMsgBodyInTcpWsDto.getFromUserId());
                        respMsg.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                        respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                        respMsg.setData(JSON.toJSONString(chatRoomMessageDTO));
                        return respMsg;
                    }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(imMsgInTcpWsBodies)) {
                log.info("没有查询到对应的直播间信息{}",reqDTO);
                throw new RuntimeException("没有查询到对应的直播间信息");
            }
            routerRpc.batchSendMsg(imMsgInTcpWsBodies);
        }
    }
}
