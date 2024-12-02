package top.mylove7.live.living.provider.sku.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;

import java.util.List;

/**
 * <p>
 * 商品sku信息表 服务类
 * </p>
 *
 * @author jiushi
 * @since 2024-08-14
 */
public interface ISkuInfoService extends IService<SkuInfo> {

    /**
     * 通过id查询Sku信息
     * @param idsList
     * @return
     */
    List<SkuInfo> queryBySkuIds(List<Long> idsList);

    /**
     * 通过skuId查询商品详情
     * @param skuId
     * @return
     */
    SkuInfo queryBySkuId(Long skuId);

    /**
     * 从缓存中查询商品详情
     * @param skuId
     * @return
     */
    SkuInfo queryBySkuIdFromCache(Long skuId);
}
