package top.mylove7.live.api.live.sku.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.mylove7.live.api.live.sku.service.IShopInfoService;
import top.mylove7.live.common.interfaces.context.JiushiLoginRequestContext;
import top.mylove7.live.common.interfaces.error.BizBaseErrorEnum;
import top.mylove7.live.common.interfaces.error.ErrorAssert;
import top.mylove7.live.common.interfaces.utils.ConvertBeanUtils;
import top.mylove7.live.living.interfaces.room.dto.LivingRoomRespDTO;
import top.mylove7.live.living.interfaces.room.rpc.ILivingRoomRpc;
import top.mylove7.live.living.interfaces.sku.dto.*;
import top.mylove7.live.living.interfaces.sku.qo.ShopCarReqVO;
import top.mylove7.live.living.interfaces.sku.qo.SkuInfoReqVO;
import top.mylove7.live.living.interfaces.sku.rpc.IShopCarRPC;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuOrderInfoRPC;
import top.mylove7.live.living.interfaces.sku.rpc.ISkuRPC;
import top.mylove7.live.living.interfaces.sku.vo.*;

import java.util.List;

@Service
public class ShopInfoServiceImpl implements IShopInfoService {
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @DubboReference
    private ISkuRPC skuRpc;

    @DubboReference
    private IShopCarRPC shopCarRPC;
    @DubboReference
    private ISkuOrderInfoRPC skuOrderInfoRPC;

    @Override
    public List<SkuInfoVO> queryByRoomId(Long roomId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        Assert.notNull(livingRoomRespDTO,"直播间不存在");
        List<SkuInfoDTO> skuInfoDTOList = skuRpc.queryByAnchorId(livingRoomRespDTO.getAnchorId());

        return ConvertBeanUtils.convertList(skuInfoDTOList, SkuInfoVO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO reqVO) {
        return ConvertBeanUtils.convert(skuRpc.queryBySkuId(reqVO.getSkuId()), SkuDetailInfoVO.class);
    }

    @Override
    public ShopCarRespVO getCarInfo(ShopCarReqVO reqVO) {
        ShopCarReqDTO reqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        reqDTO.setUserId(JiushiLoginRequestContext.getUserId());
        ShopCarRespDTO shopCarRespDTO = shopCarRPC.getCarInfo(reqDTO);
        ShopCarRespVO shopCarRespVO = ConvertBeanUtils.convert(shopCarRespDTO, ShopCarRespVO.class);
        shopCarRespVO.setShopCarItemRespVOList(ConvertBeanUtils.convertList(shopCarRespDTO.getShopCarItemRespDTOList(), ShopCarItemRespVO.class));
        return shopCarRespVO;
    }

    @Override
    public boolean addCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO reqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        reqDTO.setUserId(JiushiLoginRequestContext.getUserId());
        return shopCarRPC.addCar(reqDTO);
    }

    @Override
    public boolean removeFromCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO reqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        reqDTO.setUserId(JiushiLoginRequestContext.getUserId());
        return shopCarRPC.removeFromCar(reqDTO);
    }

    @Override
    public boolean clearCar(ShopCarReqVO reqVO) {
        ShopCarReqDTO reqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        reqDTO.setUserId(JiushiLoginRequestContext.getUserId());
        return shopCarRPC.clearCar(reqDTO);
    }

    @Override
    public boolean addCarItemNum(ShopCarReqVO reqVO) {
        ShopCarReqDTO reqDTO = ConvertBeanUtils.convert(reqVO, ShopCarReqDTO.class);
        reqDTO.setUserId(JiushiLoginRequestContext.getUserId());
        return shopCarRPC.addCarItemNum(reqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO reqVO) {
        reqVO.setUserId(JiushiLoginRequestContext.getUserId());
        return skuOrderInfoRPC.prepareOrder(ConvertBeanUtils.convert(reqVO, PrepareOrderReqDTO.class));
    }

    @Override
    public boolean payNow(PrepareOrderVO prepareOrderVO) {
        prepareOrderVO.setUserId(JiushiLoginRequestContext.getUserId());
        boolean isSuccess = skuOrderInfoRPC.payNow(ConvertBeanUtils.convert(prepareOrderVO, PayNowReqDTO.class));
        Assert.isTrue(isSuccess, "支付失败");
        return true;
    }
}
