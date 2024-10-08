package top.mylove7.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.interfaces.IPayProductRpc;
import top.mylove7.live.bank.provider.service.IPayProductService;

import java.util.List;

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
    public List<PayProductDTO> products(Integer type) {
        return payProductService.products(type);
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        return payProductService.getByProductId(productId);
    }
}
