package org.qiyu.live.im.core.server.common;



import cn.hutool.json.JSONObject;
import lombok.Data;
import top.mylove7.live.common.interfaces.constants.ImConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class ImMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = -6567417873780541989L;
    //魔数 用于做基本校验
    private short magic;

    //用于标识当前消息的作用，后续会交给不同的handler去处理
    private int code;

    //用于记录body的长度
    private int len;

    //存储消息体的内容，一般会按照字节数组的方式去存放
    private byte[] body;


    public static ImMsg build(int code,String data) {
        ImMsg imMsg = new ImMsg();
        imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
        imMsg.setCode(code);
        imMsg.setBody(data.getBytes());
        imMsg.setLen(imMsg.getBody().length);
        return imMsg;
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
