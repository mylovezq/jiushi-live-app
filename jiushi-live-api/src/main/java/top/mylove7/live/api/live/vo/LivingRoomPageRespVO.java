package top.mylove7.live.api.live.vo;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public class LivingRoomPageRespVO {

    private List<LivingRoomRespVO> list;
    private boolean hasNext;

    public List<LivingRoomRespVO> getList() {
        return list;
    }

    public void setList(List<LivingRoomRespVO> list) {
        this.list = list;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return "LivingRoomPageRespVO{" +
                "list=" + list +
                ", hasNext=" + hasNext +
                '}';
    }
}
