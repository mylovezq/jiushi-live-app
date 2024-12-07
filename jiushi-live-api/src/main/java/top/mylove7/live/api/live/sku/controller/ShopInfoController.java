package top.mylove7.live.api.live.sku.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mylove7.live.api.live.sku.service.IShopInfoService;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.vo.WebResponseVO;
import top.mylove7.live.living.interfaces.sku.qo.ShopCarReqVO;
import top.mylove7.live.living.interfaces.sku.qo.SkuInfoReqVO;
import top.mylove7.live.living.interfaces.sku.vo.PrepareOrderVO;

/**
 * <p>
 * 商品sku信息表 前端控制器
 * </p>
 *
 * @author jiushi
 * @since 2024-08-14
 */
@RestController
@RequestMapping("/shop")
public class ShopInfoController {
    @Resource
    private IShopInfoService shopInfoService;

    @PostMapping("/listSkuInfo")
    public WebResponseVO listSkuInfo(Long roomId) {
        return WebResponseVO.success(shopInfoService.queryByRoomId(roomId));
    }

    @PostMapping("/detail")
    public WebResponseVO detail(SkuInfoReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.detail(reqVO));
    }

    //用户进入直播间，查看到商品列表
    //用户查看商品详情
    //用户把感兴趣的商品，加入待支付的购物车中（购物车的概念）~>购物车的基本存储结构（按照直播间为维度去设计购物车】
    //直播间的购物车是独立的，不会存在数据跨直播间存在的情况
    //购物车的添加，移除
    //购物车的内容展示
    //购物车的清空
    @PostMapping("/addCar")
    public WebResponseVO addCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.addCar(reqVO));
    }

    @PostMapping("/removeFromCar")
    public WebResponseVO removeFromCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.removeFromCar(reqVO));
    }

    @PostMapping("/getCarInfo")
    public WebResponseVO getCarInfo(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.getCarInfo(reqVO));
    }

    @PostMapping("/clearCar")
    public WebResponseVO clearCar(ShopCarReqVO reqVO) {
        return WebResponseVO.success(shopInfoService.clearCar(reqVO));
    }

    //购物车以及塞满了，下边的逻辑是怎样的？
    //预下单，（手机产品100台，库存的预锁定操作）(生成一条待支付订单，预先扣减库存)
    //如果下单成功（库存就正常扣减了）(修改订单状态，支付超时，库存要回滚)
    //如果到达一定时间限制没有下单(100台手机，10台库存锁定，不支付，支付倒计时，库存回滚，订单状态会变成支付超时状态
    @PostMapping("prepareOrder")
    public WebResponseVO prepareOrder(PrepareOrderVO reqVO) {
        return WebResponseVO.success(shopInfoService.prepareOrder(reqVO));
    }

    @PostMapping("prepareStockInfo")
    public WebResponseVO prepareStockInfo() {
        Long userId = JiushiLoginRequestContext.getUserId();
        shopInfoService.prepareStockInfo(userId);
        return WebResponseVO.success();
    }

    @PostMapping("payNow")
    public WebResponseVO payNow(PrepareOrderVO reqVO) {
        return WebResponseVO.success(shopInfoService.payNow(reqVO));
    }
}
