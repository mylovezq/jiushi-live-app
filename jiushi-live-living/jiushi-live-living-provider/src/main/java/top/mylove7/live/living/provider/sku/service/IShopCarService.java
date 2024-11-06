package top.mylove7.live.living.provider.sku.service;


import top.mylove7.live.living.interfaces.sku.dto.ShopCarReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarRespDTO;

public interface IShopCarService {

    /**
     * 查看购物车信息
     * @param reqDTO
     * @return
     */
    ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO);

    /**
     * 添加商品到购物车中
     * @return
     */
    boolean addCar(ShopCarReqDTO reqDTO);

    /**
     * 从购物车中，删除商品
     * @return
     */
    boolean removeFromCar(ShopCarReqDTO reqDTO);

    /**
     * 清理购物车
     * @return
     */
    boolean clearCar(ShopCarReqDTO reqDTO);

    /**
     * 修改购物车中某个商品的数量
     * @return
     */
    boolean addCarItemNum(ShopCarReqDTO reqDTO);
}
