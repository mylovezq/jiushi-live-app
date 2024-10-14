package top.mylove7.live.im.router.provider.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;

import top.mylove7.live.common.interfaces.dto.ImMsgBodyInTcpWsDto;
import top.mylove7.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import top.mylove7.live.common.interfaces.constants.ImCoreServerConstants;
import top.mylove7.live.im.router.provider.service.ImRouterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean sendMsg(ImMsgBodyInTcpWsDto imMsgBodyInTcpWsDto) {
        //假设我们有100个userid -> 10台im服务器上 100个ip做分类 -> 最终ip的数量一定是<=10
        String bindAddress = stringRedisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyInTcpWsDto.getAppId() + ":" + imMsgBodyInTcpWsDto.getUserId());
        if (StringUtils.isEmpty(bindAddress)) {
            return false;
        }
        bindAddress = bindAddress.substring(0,bindAddress.indexOf("%"));
        RpcContext.getContext().set("ip", bindAddress);
        routerHandlerRpc.sendMsg(imMsgBodyInTcpWsDto);
        return true;
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyInTcpWsDto> imMsgBodyInTcpWsDtoList) {
        Long appId = imMsgBodyInTcpWsDtoList.get(0).getAppId();
        //根据userId 将不同的userId的immsgbody分类存入map
        Map<String, ImMsgBodyInTcpWsDto> userIdMsgMap
                = imMsgBodyInTcpWsDtoList
                .parallelStream()
                .collect(Collectors.toConcurrentMap(imMsgBodyInTcpWsDto-> {
                    return ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyInTcpWsDto.getAppId() + ":" + imMsgBodyInTcpWsDto.getUserId();
                },x->x));

        //批量取出每个用户绑定的ip地址
        List<String> ipList
                = Optional.ofNullable(stringRedisTemplate.opsForValue().multiGet(userIdMsgMap.keySet()))
                .orElseGet(ArrayList::new)
                .parallelStream().filter(Objects::nonNull).toList();

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

            try {
                TimeUnit.SECONDS.sleep(3L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            routerHandlerRpc.batchSendMsg(userAndIp.getValue());
        });

    }
}
