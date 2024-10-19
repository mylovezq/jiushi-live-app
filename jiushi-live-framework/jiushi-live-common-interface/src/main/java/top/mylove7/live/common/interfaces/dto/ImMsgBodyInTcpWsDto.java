package top.mylove7.live.common.interfaces.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author jiushi
 *
 * @Description 会话传输的【业务消息体】
 */
@Data
public class ImMsgBodyInTcpWsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -7657602083071950966L;
    /**
     * 接入im服务的各个业务线id
     */
    private Long appId;
    /**
     * 发给用户id
     */
    private Long toUserId;
    /**
     * 从业务服务中获取，用于在im服务建立连接的时候使用
     */
    private String token;

    /**
     * 业务标识
     */
    private int bizCode;

    /**
     * 唯一的消息id
     */
    private String msgId;

    /**
     * 来源用户
     */
    private Long fromUserId;
    /**
     * 来源的消息id唯一
     */
    private String fromMsgId;

    /**
     * 和业务服务进行消息传递
     */
    private String data;


    public byte[] toByte(){
        return JSONUtil.toJsonStr(this).getBytes();
    }

}
