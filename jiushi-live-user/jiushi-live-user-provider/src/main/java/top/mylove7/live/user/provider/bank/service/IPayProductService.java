package top.mylove7.live.user.provider.bank.service;


import top.mylove7.live.user.interfaces.bank.dto.PayProductDTO;
import top.mylove7.live.user.interfaces.bank.vo.PayProductVO;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayProductService {

    /**
     * 返回批量的商品信息
     *
     * @param type 不同的业务场景所使用的产品
     */
    PayProductVO products(Integer type);

    /**
     * 根据产品id检索
     *
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
