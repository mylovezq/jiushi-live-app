package top.mylove7.live.living.provider.gift.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.living.interfaces.gift.dto.GiftConfigDTO;
import top.mylove7.live.living.interfaces.gift.interfaces.IGiftConfigRpc;
import top.mylove7.live.living.provider.gift.service.IGiftConfigService;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class GiftConfigRpcImpl implements IGiftConfigRpc {

    @Resource
    private IGiftConfigService giftConfigService;

    @Override
    public GiftConfigDTO getByGiftId(Integer giftId) {
        return giftConfigService.getByGiftId(giftId);
    }

    @Override
    public List<GiftConfigDTO> queryGiftList() {
        return giftConfigService.queryGiftList();
    }

    @Override
    public void insertOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.insertOne(giftConfigDTO);
    }

    @Override
    public void updateOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.updateOne(giftConfigDTO);
    }
}
