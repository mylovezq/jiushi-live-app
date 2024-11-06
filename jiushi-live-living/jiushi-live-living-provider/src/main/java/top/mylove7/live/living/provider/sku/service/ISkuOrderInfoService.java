package top.mylove7.live.living.provider.sku.service;


import com.baomidou.mybatisplus.extension.service.IService;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuOrderInfoRespDTO;
import top.mylove7.live.living.provider.sku.entity.SkuOrderInfo;

/**
 * <p>
 * 商品订单表 服务类
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
public interface ISkuOrderInfoService extends IService<SkuOrderInfo> {
    /**
     * 多直播间用户订单信息查询
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO querySkuOrderInfo(Long userId, Long roomId);

    /**
     * 订单信息查询
     * @param orderId
     * @return
     */
    SkuOrderInfoRespDTO queryByOrderId(Long orderId);

    /**
     * 新增订单
     * @param skuOrderInfoReqDTO
     * @return
     */
    SkuOrderInfo insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 更新订单状态
     * @return
     */
    boolean updateStatus(SkuOrderInfoReqDTO reqDTO);


    /**
     * 删除缓存中的订单
     */
    boolean clearCacheOrder(Long userId, Long roomId);
}
