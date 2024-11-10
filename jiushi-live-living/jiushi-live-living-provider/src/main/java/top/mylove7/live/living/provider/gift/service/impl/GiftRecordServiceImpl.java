package top.mylove7.live.living.provider.gift.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.live.living.interfaces.gift.dto.GiftRecordDTO;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.provider.gift.dao.mapper.GiftRecordMapper;
import top.mylove7.live.living.provider.gift.dao.po.GiftRecordPO;
import top.mylove7.live.living.provider.gift.service.IGiftRecordService;
import org.springframework.stereotype.Service;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class GiftRecordServiceImpl implements IGiftRecordService {

    @Resource
    private GiftRecordMapper giftRecordMapper;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO,GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
