//package org.qiyu.live.api.controller;
//
//import jakarta.annotation.Resource;
//import service.mylove7.top.live.api.IGiftService;
//import req.vo.mylove7.top.live.api.GiftReqVO;
//import resp.vo.mylove7.top.live.api.GiftConfigVO;
//import vo.top.mylove7.live.common.interfaces.WebResponseVO;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * @Author jiushi
// *
// * @Description
// */
//@RestController
//@RequestMapping("/gift")
//public class GiftController {
//
//    @Resource
//    private IGiftService giftService;
//
//    /**
//     * 获取礼物列表
//     *
//     * @return
//     */
//    @PostMapping("/listGift")
//    public WebResponseVO listGift() {
//        //调用rpc的方法，检索出来礼物配置列表
//        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
//        return WebResponseVO.success(giftConfigVOS);
//    }
//
//    /**
//     * 发送礼物方法
//     * 具体实现在后边的章节会深入讲解
//     *
//     * @return
//     */
//    @PostMapping("/send")
//    public WebResponseVO send(GiftReqVO giftReqVO) {
//        return WebResponseVO.success(giftService.send(giftReqVO));
//    }
//
//}
