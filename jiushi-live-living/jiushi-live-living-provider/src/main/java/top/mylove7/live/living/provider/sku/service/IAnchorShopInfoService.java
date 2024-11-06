package top.mylove7.live.living.provider.sku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.mylove7.live.living.provider.sku.entity.AnchorShopInfo;


import java.util.List;

/**
 * <p>
 * 带货主播权限配置表 服务类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
public interface IAnchorShopInfoService extends IService<AnchorShopInfo> {

    /**
     * 更具主播id查询skuId信息
     * @param anchorId
     * @return
     */
    List<Long> querySkuIdsByAuthorId(Long anchorId);

    /**
     * 查询所有主播id
     * @return
     */
    List<Long> queryAllValidAnchorIds();

}
