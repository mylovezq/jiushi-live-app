package top.mylove7.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftConfigDTO;
import org.qiyu.live.gift.interfaces.IGiftConfigRpc;
import top.mylove7.live.gift.provider.service.IGiftConfigService;

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
