package top.mylove7.live.api.live.room.controller;

import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.mylove7.live.api.live.room.service.ILivingRoomService;
import top.mylove7.live.api.live.room.vo.LivingRoomInitVO;
import top.mylove7.live.api.live.room.qo.LivingRoomReqQo;
import top.mylove7.live.api.live.room.qo.OnlinePkReqVO;
import top.mylove7.jiushi.live.web.starter.config.RequestLimit;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.living.interfaces.sku.qo.LivingRoomReqVO;

/**
 * @Author jiushi
 *
 * @Description
 */
@RestController
@RequestMapping("/living")
public class LivingRoomController {

    @Resource
    private ILivingRoomService livingRoomService;


    /**
     * 准备生成红包雨数据
     *
     * @return
     */
    @PostMapping("/prepareRedPacket")
    @RequestLimit(limit = 1, second = 5, msg = "正在初始化中，请稍等")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.prepareRedPacket(JiushiLoginRequestContext.getUserId(), livingRoomReqVO.getRoomId()));
    }
    /**
     * 开始红包雨活动，广播直播间用户，开始抢红包
     *
     * @return
     */
    @PostMapping("/startRedPacket")
    @RequestLimit(limit = 1, second = 5, msg = "正在广播直播间用户，请稍等")
    public WebResponseVO startRedPacket(String redPacketConfigCode) {
        return WebResponseVO.success(livingRoomService.startRedPacket(JiushiLoginRequestContext.getUserId(), redPacketConfigCode));
    }
    /**
     * 领取红包
     *
     * @return
     */
    @RequestLimit(limit = 1, second = 3, msg = "手速太快，请重试")
    @PostMapping("/receiveRedPacket")
    public WebResponseVO receiveRedPacket(LivingRoomReqVO livingRoomReqVO) {
        return WebResponseVO.success(livingRoomService.receiveRedPacket(JiushiLoginRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/list")
    public WebResponseVO list(@RequestBody @Validated LivingRoomReqQo livingRoomReqQo) {
        ErrorAssert.isTure(livingRoomReqQo.getPage() > 0 && livingRoomReqQo.getPageSize() <= 100, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.list(livingRoomReqQo));
    }

    @RequestLimit(limit = 1, second = 10, msg = "开播请求过于频繁，请稍后再试")
    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type) {
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        Long roomId = livingRoomService.startingLiving(type);
        LivingRoomInitVO initVO = new LivingRoomInitVO();
        initVO.setRoomId(roomId);
        return WebResponseVO.success(initVO);
    }

    @PostMapping("/onlinePk")
    @RequestLimit(limit = 1,second = 3)
    public WebResponseVO onlinePk(@RequestBody @Validated OnlinePkReqVO onlinePkReqVO) {
        return WebResponseVO.success(livingRoomService.onlinePk(onlinePkReqVO));
    }

    @RequestLimit(limit = 1, second = 10, msg = "关播请求过于频繁，请稍后再试")
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Long roomId) {
        ErrorAssert.isNotNull(roomId, BizBaseErrorEnum.PARAM_ERROR);
        boolean closeStatus = livingRoomService.closeLiving(roomId);
        if (closeStatus) {
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }

    /**
     * 获取主播相关配置信息（只有主播才会有权限）
     *
     * @return
     */
    @PostMapping("/anchorConfig")
    public WebResponseVO<LivingRoomInitVO> anchorConfig(Long roomId) {
        return WebResponseVO.success(livingRoomService.anchorConfig(JiushiLoginRequestContext.getUserId(), roomId));
    }

    @GetMapping("/init")
    public WebResponseVO initInfo() {
        return WebResponseVO.success(livingRoomService.initInfo());
    }


}
