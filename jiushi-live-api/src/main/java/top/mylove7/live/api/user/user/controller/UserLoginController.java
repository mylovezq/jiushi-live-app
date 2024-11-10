package top.mylove7.live.api.user.user.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import top.mylove7.live.api.user.user.service.IUserLoginService;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.user.user.dto.UserDTO;
import top.mylove7.live.user.user.interfaces.IUserRpc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/userLogin")
public class UserLoginController {

    @Resource
    private IUserLoginService userLoginService;
    @Resource
    private IUserRpc userRpc;

    //发送验证码
    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone) {
        return userLoginService.sendLoginCode(phone);
    }

    //登录请求 验证码是否合法 -> 初始化注册/老用户登录
    @PostMapping("/login")
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        return userLoginService.login(phone, code, response);
    }
    @GetMapping("/batchQueryUserInfo")
    public WebResponseVO batchQueryUserInfo(String userIds) {
        List<Long> userIdList = Arrays.asList(userIds.split(",")).stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Map<Long, UserDTO> longUserDTOMap = userRpc.batchQueryUserInfo(userIdList);
        return WebResponseVO.success(longUserDTOMap);
    }

    @PutMapping("updateUserInfo")
    public WebResponseVO updateUserInfo(@RequestBody UserDTO userDTO) {
        userDTO.setUserId(1823353984983810050L);
        userDTO.setNickName("测试");
        userDTO.setAvatar("https://www.baidu.com");
        return WebResponseVO.success(userRpc.updateUserInfo(userDTO));
    }

}
