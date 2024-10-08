package top.mylove7.live.user.provider.service;

import top.mylove7.live.user.dto.UserLoginDTO;
import top.mylove7.live.user.dto.UserPhoneDTO;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IUserPhoneService {

    /**
     * 用户登录（底层会进行手机号的注册）
     *
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
