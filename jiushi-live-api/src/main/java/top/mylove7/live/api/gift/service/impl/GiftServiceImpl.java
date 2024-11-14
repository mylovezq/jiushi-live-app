package top.mylove7.live.api.gift.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import top.mylove7.live.living.interfaces.gift.dto.GiftConfigDTO;
import top.mylove7.live.living.interfaces.gift.rpc.IGiftConfigRpc;
import top.mylove7.live.living.interfaces.gift.rpc.IGiftSendRpc;
import top.mylove7.live.living.interfaces.gift.qo.GiftReqQo;
import org.springframework.stereotype.Service;
import top.mylove7.live.api.gift.service.IGiftService;
import top.mylove7.live.api.gift.vo.GiftConfigVO;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;

import java.util.List;

/**
 * @Author jiushi
 * @Description
 */
@Service
@Slf4j
public class GiftServiceImpl implements IGiftService {


    @DubboReference
    private IGiftConfigRpc giftConfigRpc;

    @DubboReference
    private IGiftSendRpc giftSendRpc;


    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOS, GiftConfigVO.class);
    }

    @Override
    public void send(GiftReqQo giftReqQo) {
        giftSendRpc.sendGift(giftReqQo);
    }
}
