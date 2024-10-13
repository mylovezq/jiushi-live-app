package top.mylove7.live.im.core.server.common;



import cn.hutool.json.JSONObject;
import lombok.Data;
import top.mylove7.live.common.interfaces.constants.ImConstants;
import top.mylove7.live.common.interfaces.constants.ImMsgCodeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class ImTcpWsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6567417873780541989L;
    //魔数 用于做基本校验
    private short magic;

    //用于标识当前消息的作用，后续会交给不同的handler去处理
    /**
     * {@link ImMsgCodeEnum}
     */
    private int code;

    //用于记录body的长度
    private int len;

    //存储消息体的内容，一般会按照字节数组的方式去存放
    private byte[] body;

    public static ImTcpWsDto build(int code, String data) {
        ImTcpWsDto imTcpWsDto = new ImTcpWsDto();
        imTcpWsDto.setMagic(ImConstants.DEFAULT_MAGIC);
        imTcpWsDto.setCode(code);
        imTcpWsDto.setBody(data.getBytes());
        imTcpWsDto.setLen(imTcpWsDto.getBody().length);
        return imTcpWsDto;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("magic", magic);
        jsonObject.set("code", code);
        jsonObject.set("len", len);
        jsonObject.set("body", new String(body));
        return jsonObject.toString();
    }


}
