package top.mylove7.live.living.provider.gift.service.bo;

/**
 * @Author jiushi
 *
 * @Description
 */
public class GiftCacheRemoveBO {

    private boolean removeListCache;
    private int giftId;

    public boolean isRemoveListCache() {
        return removeListCache;
    }

    public void setRemoveListCache(boolean removeListCache) {
        this.removeListCache = removeListCache;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    @Override
    public String toString() {
        return "GiftCacheRemoveBO{" +
                "removeListCache=" + removeListCache +
                ", giftId=" + giftId +
                '}';
    }
}
