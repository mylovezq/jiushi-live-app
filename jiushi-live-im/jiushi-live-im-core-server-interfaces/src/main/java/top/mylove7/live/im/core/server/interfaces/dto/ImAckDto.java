package top.mylove7.live.im.core.server.interfaces.dto;

import lombok.Data;

@Data
public class ImAckDto {

    private Boolean hadAck;

    private Integer retryTime;

}
