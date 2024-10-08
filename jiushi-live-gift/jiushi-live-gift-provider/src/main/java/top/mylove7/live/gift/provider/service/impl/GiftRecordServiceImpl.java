package top.mylove7.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.gift.provider.dao.mapper.GiftRecordMapper;
import top.mylove7.live.gift.provider.dao.po.GiftRecordPO;
import top.mylove7.live.gift.provider.service.IGiftRecordService;
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
