package top.mylove7.live.living.provider.sku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import top.mylove7.live.living.provider.sku.entity.SkuStockInfo;


/**
 * <p>
 * sku库存表 Mapper 接口
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
public interface SkuStockInfoMapper extends BaseMapper<SkuStockInfo> {

    @Update("upLocalDateTimet_sku_stock_info set stock_num =  #{stock_num} where skuId = #{skuId} and version = #{version}")
    int updateStockNumBySkuId(@Param("skuId") Long skuId, @Param("num") Integer stock_num, @Param("version") Integer version);

    @Update("upLocalDateTimet_sku_stock_info set stock_num = stock_num - #{num} where skuId = #{skuId} and stock_num - #{num} > 0 and version = #{version}")
    int dcrStockNumBySkuId(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("version") Integer version);

}
