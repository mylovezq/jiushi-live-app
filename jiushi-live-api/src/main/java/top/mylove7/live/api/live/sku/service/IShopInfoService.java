package top.mylove7.live.api.live.sku.service;


import top.mylove7.live.living.interfaces.sku.dto.SkuPrepareOrderInfoDTO;
import top.mylove7.live.living.interfaces.sku.qo.ShopCarReqVO;
import top.mylove7.live.living.interfaces.sku.qo.SkuInfoReqVO;
import top.mylove7.live.living.interfaces.sku.vo.PrepareOrderVO;
import top.mylove7.live.living.interfaces.sku.vo.ShopCarRespVO;
import top.mylove7.live.living.interfaces.sku.vo.SkuDetailInfoVO;
import top.mylove7.live.living.interfaces.sku.vo.SkuInfoVO;

import java.util.List;

public interface IShopInfoService {

    /**
     * 根据直播间id查询商品信息
     * @param roomId
     * @return
     */
    List<SkuInfoVO> queryByRoomId(Long roomId);


    /**
     * 查询商品详情
     * @param skuInfoReqVO
     * @return
     */
    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);

    /**
     * 查看购物车信息
     * @param reqVO
     * @return
     */
    ShopCarRespVO getCarInfo(ShopCarReqVO reqVO);

    /**
     * 添加商品到购物车中
     * @param reqVO
     * @return
     */
    boolean addCar(ShopCarReqVO reqVO);

    /**
     * 从购物车中，删除商品
     * @param reqVO
     * @return
     */
    boolean removeFromCar(ShopCarReqVO reqVO);

    /**
     * 清理购物车
     * @param reqVO
     * @return
     */
    boolean clearCar(ShopCarReqVO reqVO);

    /**
     * 修改购物车中某个商品的数量
     * @param reqVO
     * @return
     */
    boolean addCarItemNum(ShopCarReqVO reqVO);

    /**
     * 预下单接口
     * @param reqVO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO reqVO);

    /**
     * 支付
     * @param prepareOrderVO
     * @return
     */
    boolean payNow(PrepareOrderVO prepareOrderVO);
}
