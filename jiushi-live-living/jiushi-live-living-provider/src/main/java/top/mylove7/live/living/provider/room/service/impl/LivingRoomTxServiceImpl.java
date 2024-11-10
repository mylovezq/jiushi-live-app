package top.mylove7.live.living.provider.room.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.jiushi.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomReqDTO;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.provider.room.dao.mapper.LivingRoomMapper;
import top.mylove7.live.living.provider.room.dao.mapper.LivingRoomRecordMapper;
import top.mylove7.live.living.provider.room.dao.po.LivingRoomRecordPO;
import top.mylove7.live.living.provider.room.service.ILivingRoomService;
import top.mylove7.live.living.provider.room.service.ILivingRoomTxService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class LivingRoomTxServiceImpl implements ILivingRoomTxService {

    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;
    @Resource
    private LivingRoomMapper livingRoomMapper;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomService.queryByRoomId(livingRoomReqDTO.getRoomId());
        if (livingRoomRespDTO == null) {
            return false;
        }
        if (!(livingRoomRespDTO.getAnchorId().equals(livingRoomReqDTO.getAnchorId()))) {
            return false;
        }
        LivingRoomRecordPO livingRoomRecordPO = ConvertBeanUtils.convert(livingRoomRespDTO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(LocalDateTime.now());
        livingRoomRecordPO.setStatus(CommonStatusEum.INVALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        livingRoomMapper.deleteById(livingRoomRecordPO.getId());
        //移除掉直播间cache
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(livingRoomReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }
}
