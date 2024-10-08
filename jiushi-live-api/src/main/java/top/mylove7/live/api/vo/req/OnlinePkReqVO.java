package top.mylove7.live.api.vo.req;

/**
 * @Author jiushi
 *
 * @Description
 */
public class OnlinePkReqVO {

    private Integer roomId;

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "OnlinePkReqVO{" +
                "roomId=" + roomId +
                '}';
    }
}
