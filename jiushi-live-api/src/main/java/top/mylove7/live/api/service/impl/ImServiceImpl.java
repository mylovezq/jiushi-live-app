package top.mylove7.live.api.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import top.mylove7.live.account.interfaces.im.ImTokenRpc;
import top.mylove7.live.api.service.ImService;
import top.mylove7.live.api.vo.resp.ImConfigVO;
import top.mylove7.live.common.interfaces.constants.AppIdEnum;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;

import java.util.Collections;
import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class ImServiceImpl implements ImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO = new ImConfigVO();
        imConfigVO.setToken(imTokenRpc.createImLoginToken(JiushiLoginRequestContext.getUserId(), AppIdEnum.JIUSHI_LIVE_BIZ.getCode()));
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }

    private void buildImServerAddress(ImConfigVO imConfigVO) {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("jiushi-live-im-core-server");
        Collections.shuffle(serviceInstanceList);
        ServiceInstance aimInstance = serviceInstanceList.get(0);

    }
}
