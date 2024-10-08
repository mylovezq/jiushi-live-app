package top.mylove7.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.interfaces.IGiftRecordRpc;
import top.mylove7.live.gift.provider.service.IGiftRecordService;

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
