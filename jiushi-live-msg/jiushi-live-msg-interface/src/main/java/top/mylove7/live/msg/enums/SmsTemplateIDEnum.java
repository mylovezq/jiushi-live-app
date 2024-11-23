package top.mylove7.live.msg.enums;

import lombok.Data;
import lombok.Getter;

/**
 * @Author jiushi
 *
 * @Description
 */
@Getter
public enum SmsTemplateIDEnum {

    SMS_LOGIN_CODE_TEMPLATE("1","登录验证码短信模版");

    private final String templateId;
    private final String desc;

    SmsTemplateIDEnum(String templateId, String desc) {
        this.templateId = templateId;
        this.desc = desc;
    }


}
