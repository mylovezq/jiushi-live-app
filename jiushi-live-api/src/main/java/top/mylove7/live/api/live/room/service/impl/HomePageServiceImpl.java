package top.mylove7.live.api.live.room.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.mylove7.live.api.live.room.service.IHomePageService;
import top.mylove7.live.api.live.room.vo.HomePageVO;
import top.mylove7.live.user.user.dto.UserDTO;
import top.mylove7.live.user.user.interfaces.IUserRpc;
import top.mylove7.live.user.user.interfaces.IUserTagRpc;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class HomePageServiceImpl implements IHomePageService {

    @DubboReference
    private IUserRpc userRpc;
    @DubboReference
    private IUserTagRpc userTagRpc;

    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getByUserId(userId);
        HomePageVO homePageVO = new HomePageVO();
        if (userDTO != null) {
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userId);
            homePageVO.setNickName(userDTO.getNickName());
            //vip用户有权利开播
            homePageVO.setShowStartLivingBtn(true);
        }
        return homePageVO;
    }
}
