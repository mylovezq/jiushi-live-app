package top.mylove7.live.user.provider.bank.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;


import top.mylove7.live.user.interfaces.bank.dto.PayProductDTO;
import top.mylove7.live.user.interfaces.bank.interfaces.IPayProductRpc;
import top.mylove7.live.user.interfaces.bank.vo.PayProductVO;
import top.mylove7.live.user.provider.bank.service.IPayProductService;

/**
 * @Author jiushi
 *
 * @Description
 */
@DubboService
public class PayProductRpcImpl implements IPayProductRpc {

    @Resource
    private IPayProductService payProductService;

    @Override
    public PayProductVO products(Integer type) {
        return payProductService.products(type);
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        return payProductService.getByProductId(productId);
    }
}
