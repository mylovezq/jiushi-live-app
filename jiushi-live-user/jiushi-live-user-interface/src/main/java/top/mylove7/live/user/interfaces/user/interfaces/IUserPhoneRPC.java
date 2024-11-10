package top.mylove7.live.user.user.interfaces;

import top.mylove7.live.user.user.dto.UserLoginDTO;
import top.mylove7.live.user.user.dto.UserPhoneDTO;

import java.util.List;

/**
 * 用户手机相关RPC
 *
 * @Author jiushi
 *
 * @Description
 */
public interface IUserPhoneRPC {

    /**
     * 用户登录（底层会进行手机号的注册）
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);

    /**
     * 根据手机信息查询相关用户信息
     *
     * @param phone
     * @return
     */
    UserPhoneDTO queryByPhone(String phone);

    /**
     * 根据用户id查询手机相关信息
     *
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
