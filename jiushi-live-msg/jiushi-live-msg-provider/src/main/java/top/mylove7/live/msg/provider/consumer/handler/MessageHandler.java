package top.mylove7.live.msg.provider.consumer.handler;


import top.mylove7.live.common.interfaces.dto.ImMsgBody;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface MessageHandler {

    /**
     * 处理im服务投递过来的消息内容
     *
     * @param imMsgBody
     */
    void onMsgReceive(ImMsgBody imMsgBody);
}
