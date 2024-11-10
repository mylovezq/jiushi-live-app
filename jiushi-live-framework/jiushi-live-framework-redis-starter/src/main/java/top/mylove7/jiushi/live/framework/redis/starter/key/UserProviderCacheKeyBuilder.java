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
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_INFO_KEY = "userInfo";
    private static String USER_TAG_KEY = "userTag";
    private static String USER_TAG_LOCK_KEY = "userTagLock";
    private static String USER_PHONE_LIST_KEY = "userPhoneList";
    private static String USER_PHONE_OBJ_KEY = "userPhoneObj";
    private static String USER_LOGIN_TOKEN_KEY = "userLoginToken";
    private static String ACCOUNT_TOKEN_KEY = "account";
    private static String IM_LOGIN_TOKEN = "imLoginToken";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }

    public String buildTagLockKey(Long userId) {
        return super.getPrefix() + USER_TAG_LOCK_KEY + super.getSplitItem() + userId;
    }

    public String buildTagKey(Long userId) {
        return super.getPrefix() + USER_TAG_KEY + super.getSplitItem() + userId;
    }

    public String buildUserPhoneListKey(Long userId) {
        return super.getPrefix() + USER_PHONE_LIST_KEY + super.getSplitItem() + userId;
    }

    public String buildUserPhoneObjKey(String phone) {
        return super.getPrefix() + USER_PHONE_OBJ_KEY + super.getSplitItem() + phone;
    }

    public String buildUserLoginTokenKey(String tokenKey) {
        return super.getPrefix() + USER_LOGIN_TOKEN_KEY + super.getSplitItem() + tokenKey;
    }


    public String buildImLoginTokenKey(String token) {
        return super.getPrefix() + IM_LOGIN_TOKEN + super.getSplitItem() + token;
    }






    private static String BALANCE_CACHE = "balance_cache";
    private static String GIFT_CONSUME_KEY = "gift_consume_key";
    private static String PAY_PRODUCT_CACHE = "pay_product_cache";

    private static String PAY_PRODUCT_ITEM_CACHE = "pay_product_item_cache";


    public String buildPayProductItemCache(Integer productId) {
        return super.getPrefix() + PAY_PRODUCT_ITEM_CACHE + super.getSplitItem() + productId;
    }

    /**
     * 按照产品的类型来进行检索
     *
     * @param type
     * @return
     */
    public String buildPayProductCache(Integer type) {
        return super.getPrefix() + PAY_PRODUCT_CACHE + super.getSplitItem() + type;
    }

    /**
     * 构建用户余额cache key
     *
     * @param userId
     * @return
     */
    public String buildUserBalance(Long userId) {
        return super.getPrefix() + BALANCE_CACHE + super.getSplitItem() + userId;
    }
    public String buildGiftConsumeKey(String uuid) {
        return super.getPrefix() + GIFT_CONSUME_KEY + super.getSplitItem() + uuid;
    }
}
