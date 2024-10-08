package org.qiyu.live.im.core.server.common;

import io.netty.util.AttributeKey;

/**
 * @Author jiushi
 *
 * @Description
 */
public class ImContextAttr {

    /**
     * 绑定用户id
     */
    public static AttributeKey<Long> USER_ID = AttributeKey.valueOf("userId");

    /**
     * 绑定appId
     */
    public static AttributeKey<Long> APP_ID = AttributeKey.valueOf("appId");

    /**
     * 绑定直播间id
     */
    public static AttributeKey<Long> ROOM_ID = AttributeKey.valueOf("roomId");
}
