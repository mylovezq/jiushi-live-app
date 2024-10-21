package top.mylove7.live.api.gift.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mylove7.jiushi.live.web.starter.config.RequestLimit;
import top.mylove7.live.api.gift.service.IGiftService;
import org.qiyu.live.gift.qo.GiftReqQo;
import top.mylove7.live.api.gift.vo.GiftConfigVO;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/gift")
public class GiftController {

    @Resource
    private IGiftService giftService;

    /**
     * 获取礼物列表
     *
     * @return
     */
    @PostMapping("/listGift")
    public WebResponseVO listGift() {
        //调用rpc的方法，检索出来礼物配置列表
        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }

    /**
     * 发送礼物方法
     * 具体实现在后边的章节会深入讲解
     *
     * @return
     */
    @PostMapping("/send")
    @RequestLimit(limit = 3, second = 5, msg = "请勿频繁发送礼物")
    public WebResponseVO send(GiftReqQo giftReqQo) {
        giftReqQo.setSenderUserId(JiushiLoginRequestContext.getUserId());
        giftService.send(giftReqQo);
        return WebResponseVO.success();
    }

}
