package top.mylove7.live.living.provider.sku.service;



import com.baomidou.mybatisplus.extension.service.IService;
import top.mylove7.live.living.interfaces.sku.dto.RockBackInfoDTO;
import top.mylove7.live.living.provider.sku.entity.SkuStockInfo;
import top.mylove7.live.living.interfaces.sku.dto.UpdateStockNumDto;

import java.util.List;

/**
 * <p>
 * sku库存表 服务类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
public interface ISkuStockInfoService extends IService<SkuStockInfo> {

    /**
     * 通过skuId查询库存信息
     * @param skuId
     * @return
     */
    SkuStockInfo queryBySkuId(Long skuId);

    /**
     * 批量sku信息查询
     * @param skuIdList
     * @return
     */
    List<SkuStockInfo> queryBySkuIds(List<Long> skuIdList);

    /**
     * 更新sku库存
     *
     * @param skuId
     * @param stockNum
     */
    void updateStockNumBySkuId(Long skuId, Integer stockNum);

    /**
     * 减sku库存 db
     * @param skuId
     * @param num
     * @return
     */
    UpdateStockNumDto decrStockNumBySkuIdDB(Long skuId, Integer num);

    /**
     * 扣件sku库存 redis
     * @param skuIdList
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdCache(List<Long> skuIdList, Integer num);

    /**
     * 扣件sku库存 redis
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdCache(Long skuId, Integer num);

    /**
     * 库存回滚逻辑处理
     * @param rockBackInfoDTO
     * @return
     */
    boolean stockRollbackHandler(RockBackInfoDTO rockBackInfoDTO);
}
