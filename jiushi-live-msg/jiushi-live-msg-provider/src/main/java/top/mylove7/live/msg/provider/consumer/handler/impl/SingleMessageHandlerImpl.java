package top.mylove7.live.msg.provider.consumer.handler.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;

import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.dto.ImMsgBody;
import top.mylove7.live.im.router.interfaces.rpc.ImRouterRpc;
import org.qiyu.live.msg.dto.MessageDTO;
import top.mylove7.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import top.mylove7.live.living.interfaces.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.rpc.ILivingRoomRpc;
import top.mylove7.live.msg.provider.consumer.handler.MessageHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 *
 * @Description
 */
@Component

public class SingleMessageHandlerImpl implements MessageHandler {

   private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SingleMessageHandlerImpl.class);

    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        int bizCode = imMsgBody.getBizCode();
        LOGGER.info("im消息-msg-provider服务收到消息，消息内容：{}", imMsgBody);
        //直播间的聊天消息
        if (ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode() == bizCode) {
            //一个人发送 n个人接收
            // 根据roomId，appId 去调用rpc方法，获取对应的直播间内的userId
            // 创建一个list的imMsgBody对象，
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Long roomId = messageDTO.getRoomId();
            LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
            reqDTO.setRoomId(roomId);
            reqDTO.setAppId(imMsgBody.getAppId());
            //自己不用发
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO).stream().filter(x->!x.equals(imMsgBody.getUserId())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(userIdList)) {
                return;
            }
            List<ImMsgBody> imMsgBodies = new ArrayList<>();
            userIdList.forEach(userId -> {
                ImMsgBody respMsg = new ImMsgBody();
                respMsg.setUserId(userId);
                respMsg.setAppId(AppIdEnum.JIUSHI_LIVE_BIZ.getCode());
                respMsg.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respMsg.setData(JSON.toJSONString(messageDTO));
                imMsgBodies.add(respMsg);
            });
            //暂时不做过多的处理
            routerRpc.batchSendMsg(imMsgBodies);
        }
    }
}
