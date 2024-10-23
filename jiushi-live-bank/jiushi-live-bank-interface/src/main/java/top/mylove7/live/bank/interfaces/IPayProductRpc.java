package top.mylove7.live.bank.interfaces;

import top.mylove7.live.bank.dto.PayProductDTO;
import top.mylove7.live.bank.vo.PayProductVO;

import java.util.List;

/**
 * @Author jiushi
 *
 * @Description
 */
public interface IPayProductRpc {

    /**
     * 返回批量的商品信息
     *
     * @param type 不同的业务场景所使用的产品
     */
    PayProductVO products(Integer type);


    /**
     * 根据产品id查询
     *
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
