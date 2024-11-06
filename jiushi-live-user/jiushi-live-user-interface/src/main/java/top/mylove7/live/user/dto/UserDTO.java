package top.mylove7.live.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author jiushi
 *
 * @Description
 */
@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9144025905355048277L;

    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;


}
