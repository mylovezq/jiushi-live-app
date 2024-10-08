package top.mylove7.live.im.router.provider.service;



import top.mylove7.live.common.interfaces.dto.ImMsgBody;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImRouterService {


    /**
     * 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息，群聊场景
     *
     * @param imMsgBody
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
