package top.mylove7.live.msg.provider.service.impl;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;

import org.springframework.util.Assert;
import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.mylove7.live.im.core.server.interfaces.rpc.IImCoreRouterHandlerRpc;
import top.mylove7.live.msg.provider.service.MatchImCoreNettyMsgRouterService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @Author jiushi
 *
 * @Description
 */
@Service
@Slf4j
public class MatchImCoreNettyRouterServiceImpl implements MatchImCoreNettyMsgRouterService {

    @DubboReference
    private IImCoreRouterHandlerRpc routerHandlerRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        //假设我们有100个userid -> 10台im服务器上 100个ip做分类 -> 最终ip的数量一定是<=10
        String bindAddress = stringRedisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyInTcpWsDto.getAppId() + ":" + imMsgBodyInTcpWsDto.getToUserId());
        if (StringUtils.isEmpty(bindAddress)) {
            return false;
        }
        bindAddress = bindAddress.substring(0,bindAddress.indexOf("%"));
        RpcContext.getContext().set("ip", bindAddress);
        routerHandlerRpc.sendMsg(imMsgBodyInTcpWsDto);
        return true;
    }

    /**
     * 把要发送个n个人小消息分组，同一个ip的userId在一个组里，然后同一个ip的用户一起发送
     * @param imMsgBodyInTcpWsDtoList
     */
    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        if (CollUtil.isEmpty(imMsgBodyInTcpWsDtoList)){
            throw new RuntimeException("直播间过期，请重新进入");
        }
        Long appId = imMsgBodyInTcpWsDtoList.get(0).getAppId();
        //根据userId 将不同的userId的immsgbody分类存入map
        Map<String, ImMsgBodyInTcpWsDto> userIdMsgMap
                = imMsgBodyInTcpWsDtoList
                .parallelStream()
                .collect(Collectors.toConcurrentMap(imMsgBodyInTcpWsDto-> {
                    return ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyInTcpWsDto.getAppId() + ":" + imMsgBodyInTcpWsDto.getToUserId();
                },x->x));

        //批量取出每个用户绑定的ip地址
        List<String> ipList
                = Optional.ofNullable(stringRedisTemplate.opsForValue().multiGet(userIdMsgMap.keySet()))
                .orElseGet(ArrayList::new)
                .parallelStream().filter(Objects::nonNull).toList();
        log.info("====用户绑定的ip地址==={}",ipList);


        Map<String, List<ImMsgBodyInTcpWsDto>> userIdMap = new ConcurrentHashMap<>();
        ipList.parallelStream().forEach(ipStr -> {
            String[] ipAndUserId = ipStr.split("%");
            String ip = ipAndUserId[0];
            String userId = ipAndUserId[1];
            ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto = userIdMsgMap.get(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
            userIdMap.computeIfAbsent(ip, value -> new ArrayList<>()).add(imMsgBodyInTcpWsDto);
        });

        //将连接同一台ip地址的imMsgBody组装到同一个list集合中，然后进行统一的发送
        userIdMap.entrySet().parallelStream().forEach(userAndIp->{
            RpcContext.getContext().set("ip", userAndIp.getKey());
            //指定这个ip的routerHandlerRpc发送消息
            routerHandlerRpc.batchSendMsg(userAndIp.getValue());
        });

    }
}
