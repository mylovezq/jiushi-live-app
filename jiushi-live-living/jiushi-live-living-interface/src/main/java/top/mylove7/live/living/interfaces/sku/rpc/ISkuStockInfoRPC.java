package top.mylove7.live.living.interfaces.sku.rpc;


import top.mylove7.live.living.interfaces.sku.dto.SkuStockInfoDTO;

public interface ISkuStockInfoRPC {

    /**
     * 通过skuId查询库存信息
     * @param skuId
     * @return
     */
    SkuStockInfoDTO queryBySkuId(Long skuId);

    //库存值从mysql预热加载到redis中

    /**
     * 预热库存信息
     * @param anchorId
     * @return
     */
    boolean prepareStockInfo(Long anchorId);

    //提供基础的缓存查询接口

    /**
     * 基础的缓存查询接口
     * @param skuId
     * @return
     */
    int queryStockNum(Long skuId);
    //设计一个接口用于同步redis值到mysql中（定时任务执行，本地定时任务去完成同步行为）

    /**
     * 同步库存信息到MySql中
     * @param anchorId
     * @return
     */
    boolean syncStockNumToMySql(Long anchorId);
    //库存扣减要设计Lua脚本

    /**
     * 更新sku库存
     * @param skuId
     * @param num
     * @return
     */
    boolean updateStockNumBySkuId(Long skuId, Integer num);
    //库存扣减要设计Lua脚本

    /**
     * 扣件sku库存 Redis
     * @param skuId
     * @param num
     * @return
     */
    boolean dcrStockNumBySkuIdCache(Long skuId, Integer num);
    //库存扣减成功后，生成待支付订单(MQ)
}
