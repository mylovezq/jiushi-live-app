package top.mylove7.live.living.interfaces.sku.rpc;


import top.mylove7.live.living.interfaces.sku.dto.SkuInfoDTO;
import top.mylove7.live.living.interfaces.sku.dto.SkuDetailInfoDTO;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 21:20 2023/7/19
 * @Description
 */
public interface ISkuRPC {

    /**
     * 通过id查询Sku信息
     * @param anchorId
     * @return
     */
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);

    /**
     * 通过skuId查询商品详情
     * @param skuId
     * @return
     */
    SkuDetailInfoDTO queryBySkuId(Long skuId);
}
