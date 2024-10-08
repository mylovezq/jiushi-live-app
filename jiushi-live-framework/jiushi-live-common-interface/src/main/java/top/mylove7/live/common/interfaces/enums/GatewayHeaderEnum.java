package top.mylove7.live.common.interfaces.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关服务传递给下游的header枚举
 *
 * @Author jiushi
 *
 * @Description
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GatewayHeaderEnum {

    USER_LOGIN_ID("用户id","jiushi_user_id"),
    IM_USER_LOGIN_INFO("im用户信息","jiushi_im_user");

    String desc;
    String name;


}
