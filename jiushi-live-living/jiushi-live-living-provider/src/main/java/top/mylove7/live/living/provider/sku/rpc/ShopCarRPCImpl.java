package top.mylove7.live.living.provider.sku.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarReqDTO;
import top.mylove7.live.living.interfaces.sku.dto.ShopCarRespDTO;
import top.mylove7.live.living.interfaces.sku.rpc.IShopCarRPC;
import top.mylove7.live.living.provider.sku.service.IShopCarService;

/**
 * @Program: qiyu-live-app
 *
 * @Description:
 *
 * @Author: tangfh
 *
 * @Create: 2024-08-15 14:46
 */
@DubboService
public class ShopCarRPCImpl implements IShopCarRPC {

    @Resource
    IShopCarService shopCarService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO reqDTO) {
        return shopCarService.getCarInfo(reqDTO);
    }

    @Override
    public boolean addCar(ShopCarReqDTO reqDTO) {
        return shopCarService.addCar(reqDTO);
    }

    @Override
    public boolean removeFromCar(ShopCarReqDTO reqDTO) {
        return shopCarService.removeFromCar(reqDTO);
    }

    @Override
    public boolean clearCar(ShopCarReqDTO reqDTO) {
        return shopCarService.clearCar(reqDTO);
    }

    @Override
    public boolean addCarItemNum(ShopCarReqDTO reqDTO) {
        return shopCarService.addCarItemNum(reqDTO);
    }


}
