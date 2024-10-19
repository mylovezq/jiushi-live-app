package top.mylove7.live.im.core.server.dao;

import com.anwen.mongo.annotation.ID;
import com.anwen.mongo.annotation.collection.CollectionName;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 使用@Document注解指定集合名称
 */
@CollectionName("im_msg_rerecord")
@Data
@NoArgsConstructor
public class ImMsgMongoDo implements Serializable {
    private static final long serialVersionUID = -3258839839160856613L;
    /**
     * 使用@Id注解指定MongoDB中的 _id 主键
     */
    @ID
    private Long id;

    private Long appId;

    private Long fromUserId;

    private Long toUserId;

    private Long roomId;

    private String messageId;


    private byte[] messageContent;

    private Integer retryTime;

    private Boolean hadAck;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public ImMsgMongoDo (Long appId,Long fromUserId,Long toUserId,Long roomId,String messageId,byte[] messageContent ){
    	this.appId=appId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.roomId = roomId;
        this.messageId = messageId;
        this.messageContent = messageContent;
    }


}