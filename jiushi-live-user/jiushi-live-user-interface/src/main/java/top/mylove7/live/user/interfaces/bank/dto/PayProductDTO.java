package top.mylove7.live.user.interfaces.bank.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Author jiushi
 *
 * @Description
 */
@Data
public class PayProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8737032096039095954L;
    private Long id;
    private String name;
    private Integer price;
    private String extra;
    private Integer type;
    private Integer validStatus;
    private Date createTime;
    private Date updateTime;
}
