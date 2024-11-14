package top.mylove7.live.living.provider.sku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.mylove7.live.living.provider.sku.entity.SkuInfo;


import java.util.List;

/**
 * <p>
 * 商品sku信息表 Mapper 接口
 * </p>
 *
 * @author tangfh
 * @since 2024-08-14
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

}
