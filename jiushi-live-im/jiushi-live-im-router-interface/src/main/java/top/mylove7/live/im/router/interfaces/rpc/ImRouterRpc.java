package top.mylove7.live.im.router.interfaces.rpc;




import top.mylove7.live.common.interfaces.dto.ImMsgBody;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface ImRouterRpc {


    /**
     * 发送消息
     *
     * @param imMsgBody
     * @return
     */
    boolean sendMsg(ImMsgBody imMsgBody);


    /**
     * 批量发送消息，在直播间内
     *
     * @param imMsgBody
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
