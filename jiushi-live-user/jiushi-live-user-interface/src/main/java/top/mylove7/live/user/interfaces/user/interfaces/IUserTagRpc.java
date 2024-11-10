package top.mylove7.live.user.user.interfaces;


import top.mylove7.live.user.user.constants.UserTagsEnum;

/**
 * @Author jiushi
 *
 * @Description 用户标签RPC服务
 */
public interface IUserTagRpc {

   /**
    * 设置标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean setTag(Long userId, UserTagsEnum userTagsEnum);

   /**
    * 取消标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean cancelTag(Long userId,UserTagsEnum userTagsEnum);

   /**
    * 是否包含某个标签
    *
    * @param userId
    * @param userTagsEnum
    * @return
    */
   boolean containTag(Long userId,UserTagsEnum userTagsEnum);
}
