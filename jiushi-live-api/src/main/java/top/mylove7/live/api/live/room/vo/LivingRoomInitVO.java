package top.mylove7.live.api.live.room.vo;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class LivingRoomInitVO {

    private Long anchorId;
    private Long userId;
    private String anchorImg;
    private String roomName;
    private boolean isAnchor;
    private String avatar;
    private Long roomId;
    private String watcherNickName;
    private String anchorNickName;
    //观众头像
    private String watcherAvatar;
    //默认背景图，为了方便讲解使用
    private String defaultBgImg;
    private Long pkObjId;

    private String  redPacketConfigCode;

}
