package top.mylove7.live.api.im.controller;

import cn.hutool.core.util.IdUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.mylove7.live.api.im.service.impl.SSEService;

@Controller
@RequestMapping("notice")
public class TestController {
    @Autowired
    private SSEService sseService;


    @GetMapping(value = "/createSse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createSse(String uid) {
        return sseService.crateSse(uid);
    }

    @GetMapping("sendMsg")
    @ResponseBody
    @CrossOrigin
    public String sseChat(String uid) {
        for (int i = 0; i < 10; i++) {
            sseService.sendMessage(uid, "消息" + i, IdUtil.fastUUID().replace("-", ""));
        }
        return "OK";
    }

    @GetMapping("closeSse")
    @CrossOrigin
    public void closeSse(String uid) {
        sseService.closeSse(uid);
    }
}