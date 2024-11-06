package top.mylove7.live.living.provider.sku.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.sku.dto.SkuDetailInfoDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuInfoDTO;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuRPC;
import top.mylove7.live.living.provider.sku.service.IAnchorShopInfoService;
import top.mylove7.live.living.provider.sku.service.ISkuInfoService;


import java.util.List;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-14 17:50
 */
@DubboService
public class SkuRPCImpl implements ISkuRPC {

    @Resource
    private ISkuInfoService skuInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;

    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        List<Long> idsList = anchorShopInfoService.querySkuIdsByAuthorId(anchorId);
        return ConvertBeanUtils.convertList(skuInfoService.queryBySkuIds(idsList), SkuInfoDTO.class);
    }

    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId) {
        return ConvertBeanUtils.convert(skuInfoService.queryBySkuIdFromCache(skuId), SkuDetailInfoDTO.class);
    }

}
