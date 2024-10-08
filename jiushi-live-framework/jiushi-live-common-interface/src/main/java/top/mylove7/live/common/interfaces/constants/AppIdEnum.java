package top.mylove7.live.common.interfaces.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author jiushi
 *
 * @Description
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AppIdEnum {

    JIUSHI_LIVE_BIZ(10001L,"旗鱼直播业务");

    Long code;
    String desc;


}
