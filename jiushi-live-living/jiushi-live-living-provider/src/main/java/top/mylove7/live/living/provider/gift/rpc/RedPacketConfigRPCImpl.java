package top.mylove7.live.living.provider.gift.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigReqDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigRespDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveDTO;
import top.mylove7.live.living.interfaces.gift.rpc.IRedPacketConfigRPC;
import top.mylove7.live.living.provider.gift.dao.po.RedPacketConfigPO;
import top.mylove7.live.living.provider.gift.service.IRedPacketConfigService;

@DubboService
public class RedPacketConfigRPCImpl implements IRedPacketConfigRPC {

    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAuthorId(Long authorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAuthorId(authorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigRespDTO redPacketConfigRespDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigRespDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean updateById(RedPacketConfigRespDTO redPacketConfigRespDTO) {
        return redPacketConfigService.updateById(ConvertBeanUtils.convert(redPacketConfigRespDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long authorId) {
        return redPacketConfigService.prepareRedPacket(authorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.receiveRedPacket(reqDTO);
    }

    @Override
    public boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
