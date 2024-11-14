package top.mylove7.live.msg.provider.consumer;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.msg.constants.ImMsgBizCodeEnum;
import top.mylove7.live.msg.dto.ChatRoomMessageDTO;
import top.mylove7.live.msg.provider.service.MatchImCoreNettyMsgRouterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static top.mylove7.live.common.interfaces.topic.ImCoreServerProviderTopicNames.JIUSHI_LIVE_IM_BIZ_MSG_TOPIC;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = JIUSHI_LIVE_IM_BIZ_MSG_TOPIC,
        consumerGroup = "${spring.application.name}_ImBizMsgListener",
        messageModel = MessageModel.CLUSTERING)
public class ImBizMsgListener implements RocketMQListener<ImMsgBodyInTcpWsDto> {

    @Resource
    private MatchImCoreNettyMsgRouterService matchImCoreNettyMsgRouterService;

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    /**
     * 消费消息的方法
     *
     * @param imMsgBodyInTcpWsDto 消息内容，类型和上面的泛型一致。如果泛型指定了固定的类型，消息体就是我们的参数
     */
    @Override
    public void onMessage(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        int bizCode = imMsgBodyInTcpWsDto.getBizCode();
        //直播间的聊天消息
        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() == bizCode) {
            this.handlerImChatMsgBiz(imMsgBodyInTcpWsDto);
            return;
        }

        log.info("mq业务消息触发接受处理成功");
    }
    private void handlerImChatMsgBiz(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {

        // 根据roomId，appId 去调用rpc方法，获取对应的直播间内的userId
        ChatRoomMessageDTO chatRoomMessageDTO = JSON.parseObject(imMsgBodyInTcpWsDto.getData(), ChatRoomMessageDTO.class);
        log.info("直播室的im消息{}", chatRoomMessageDTO);

        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setRoomId(chatRoomMessageDTO.getRoomId());
        reqDTO.setAppId(imMsgBodyInTcpWsDto.getAppId());
        List<Long> userIds =  Optional.ofNullable(livingRoomRpc.queryUserIdByRoomId(reqDTO)).orElse(new ArrayList<>());
        log.info("根据直播间{}，查询出要发送消息的用户id{}",chatRoomMessageDTO.getRoomId(), userIds);
        //自己不用发
        //一个人发送 n个人接收，封装出n个人要接收到imMsgBody对象list

        List<ImMsgBodyInTcpWsDto> imMsgInTcpWsBodies
                = userIds
                .parallelStream()
                .filter(userId -> !Objects.equals(imMsgBodyInTcpWsDto.getFromUserId(), userId))
                .map(toUserId -> {
                    ImMsgBodyInTcpWsDto respMsg = new ImMsgBodyInTcpWsDto();
                    respMsg.setToUserId(toUserId);
                    respMsg.setFromUserId(imMsgBodyInTcpWsDto.getFromUserId());
                    respMsg.setMsgId(imMsgBodyInTcpWsDto.getMsgId());
                    respMsg.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                    respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                    respMsg.setData(JSON.toJSONString(chatRoomMessageDTO));
                    return respMsg;
                }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(imMsgInTcpWsBodies)) {
            log.info("没有查询到对应的直播间信息{}", reqDTO);
            throw new RuntimeException("没有查询到对应的直播间信息");
        }
        matchImCoreNettyMsgRouterService.batchSendMsg(imMsgInTcpWsBodies);
    }


}
