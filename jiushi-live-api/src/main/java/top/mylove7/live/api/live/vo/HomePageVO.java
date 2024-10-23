package top.mylove7.live.api.live.vo;

import lombok.Data;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class HomePageVO {

    private boolean loginStatus;
    private Long userId;
    private String nickName;
    private String avatar;
    //是否是主播身份
    private boolean showStartLivingBtn;
}
