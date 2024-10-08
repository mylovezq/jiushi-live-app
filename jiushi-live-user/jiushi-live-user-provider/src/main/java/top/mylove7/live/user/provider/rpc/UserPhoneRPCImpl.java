package top.mylove7.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.user.dto.UserLoginDTO;
import top.mylove7.live.user.dto.UserPhoneDTO;
import top.mylove7.live.user.interfaces.IUserPhoneRPC;
import top.mylove7.live.user.provider.service.IUserPhoneService;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class UserPhoneRPCImpl implements IUserPhoneRPC {

    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }
}
