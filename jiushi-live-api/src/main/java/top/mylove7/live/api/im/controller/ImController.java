package top.mylove7.live.api.im.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mylove7.live.api.im.service.ImService;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;

/**
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/im")
public class ImController {

    @Resource
    private ImService imService;

    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig() {
        return WebResponseVO.success(imService.getImConfig());
    }
}
