package top.mylove7.jiushi.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author jiushi
 *
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class LivingProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String LIVING_ROOM_LIST = "living_room_list";
    private static String LIVING_ROOM_OBJ = "living_room_obj";
    private static String REFRESH_LIVING_ROOM_LIST_LOCK = "refresh_living_room_list_lock";
    private static String LIVING_ROOM_USER_SET = "living_room_user_set";
    private static String LIVING_ONLINE_PK = "living_online_pk";


    private static String GIFT_CONFIG_CACHE = "gift_config_cache";
    private static String GIFT_LIST_CACHE = "gift_list_cache";
    private static String GIFT_CONSUME_KEY = "gift_consume_key";
    private static String GIFT_LIST_LOCK = "gift_list_lock";
    private static String LIVING_PK_IS_OVER = "living_pk_is_over";
    private static String LIVING_PK_KEY = "living_pk_key";




    private static String SKU_DETAIL = "sku_detail";
    private static String SHOP_Car = "shop_Car";
    private static String SKU_STOCK = "sku_stock";
    private static String SKU_STOCK_SYNC_LOCK = "sku_stock_sync_lock";
    private static String SKU_ORDER_INFO = "sku_order_info";
    private static String SKU_ORDER_INFO_BY_ORDER_ID = "sku_order_info_by_order_id";

    public String buildLivingOnlinePk(Long roomId) {
        return super.getPrefix() + LIVING_ONLINE_PK + super.getSplitItem() + roomId;
    }

    public String buildLivingRoomUserSet(Long roomId, Long appId) {
        return super.getPrefix() + LIVING_ROOM_USER_SET + super.getSplitItem() + appId + super.getSplitItem() + roomId;
    }

    public String buildRefreshLivingRoomListLock() {
        return super.getPrefix() + REFRESH_LIVING_ROOM_LIST_LOCK;
    }

    public String buildLivingRoomObj(Long roomId) {
        return super.getPrefix() + LIVING_ROOM_OBJ + super.getSplitItem() + roomId;
    }

    public String buildLivingRoomList(Integer type) {
        return super.getPrefix() + LIVING_ROOM_LIST + super.getSplitItem() + type;
    }




    public String buildGiftConsumeKey(String uuid) {
        return super.getPrefix() + GIFT_CONSUME_KEY + super.getSplitItem() + uuid;
    }

    public String buildGiftConfigCacheKey(int giftId) {
        return super.getPrefix() + GIFT_CONFIG_CACHE + super.getSplitItem() + giftId;
    }

    public String buildGiftListCacheKey() {
        return super.getPrefix() + GIFT_LIST_CACHE;
    }

    public String buildGiftListLockCacheKey() {
        return super.getPrefix() + GIFT_LIST_LOCK;
    }


    public String buildLivingPkIsOver(Long roomId) {
        return super.getPrefix() + LIVING_PK_IS_OVER + super.getSplitItem() + roomId;
    }
    public String buildLivingPkKey(Long roomId) {
        return super.getPrefix() + LIVING_PK_KEY + super.getSplitItem() + roomId;
    }


    public String buildSkuOrderInfoByOrderId(Long orderId) {
        return super.getPrefix() + SKU_ORDER_INFO_BY_ORDER_ID + super.getSplitItem() + orderId;
    }
    public String buildSkuOrderInfo(Long userId, Long roomId) {
        return super.getPrefix() + SKU_ORDER_INFO + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }
    public String buildSkuStockSyncLock() {
        return super.getPrefix() + SKU_STOCK_SYNC_LOCK;
    }
    public String buildSkuStock(Long skuId) {
        return super.getPrefix() + SKU_STOCK + super.getSplitItem() + skuId;
    }

    public String buildShopCar(Long userId, Long roomId) {
        return super.getPrefix() + SHOP_Car + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }

    public String buildSkuDetail(Long skuId) {
        return super.getPrefix() + SKU_DETAIL + super.getSplitItem() + skuId;
    }
}
