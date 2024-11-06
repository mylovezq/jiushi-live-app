package top.mylove7.live.living.provider.sku.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.mylove7.live.common.interfaces.enums.CommonStatusEum;
import top.mylove7.live.living.provider.sku.entity.AnchorShopInfo;
import top.mylove7.live.living.provider.sku.mapper.AnchorShopInfoMapper;
import top.mylove7.live.living.provider.sku.service.IAnchorShopInfoService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 带货主播权限配置表 服务实现类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@Service
public class AnchorShopInfoServiceImpl extends ServiceImpl<AnchorShopInfoMapper, AnchorShopInfo> implements IAnchorShopInfoService {

    @Override
    public List<Long> querySkuIdsByAuthorId(Long anchorId) {
        LambdaQueryWrapper<AnchorShopInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfo::getAnchorId, anchorId);
        queryWrapper.eq(AnchorShopInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return list(queryWrapper).stream().map(AnchorShopInfo::getSkuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryAllValidAnchorIds() {
        LambdaQueryWrapper<AnchorShopInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfo::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        //可以加时间条件缩小范围
        return list(queryWrapper).stream().map(AnchorShopInfo::getAnchorId).collect(Collectors.toList());
    }
}
