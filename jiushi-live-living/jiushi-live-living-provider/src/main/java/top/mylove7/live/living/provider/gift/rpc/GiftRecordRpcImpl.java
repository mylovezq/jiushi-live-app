package top.mylove7.live.living.provider.gift.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.living.interfaces.gift.dto.GiftRecordDTO;
import top.mylove7.live.living.interfaces.gift.rpc.IGiftRecordRpc;
import top.mylove7.live.living.provider.gift.service.IGiftRecordService;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {

    @Resource
    private IGiftRecordService giftRecordService;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        giftRecordService.insertOne(giftRecordDTO);
    }
}
