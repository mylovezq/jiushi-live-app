package top.mylove7.live.living.provider.gift.service;


import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigReqDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveDTO;
import top.mylove7.live.living.provider.gift.dao.po.RedPacketConfigPO;

public interface IRedPacketConfigService {

    /**
     * 根据主播id查询是否有红包雨配置特权
     * @param authorId
     * @return
     */
    RedPacketConfigPO queryByAuthorId(Long authorId);

    /**
     * 根据红包雨配置code检索信息
     * @param code
     * @return
     */
    RedPacketConfigPO queryByConfigCode(String code);

    /**
     * 新增配置
     * @param redPacketConfigPO
     * @return
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新配置
     * @param redPacketConfigPO
     * @return
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    /**
     * 生成红包雨数据
     * @param authorId
     * @return
     */
    boolean prepareRedPacket(Long authorId);

    /**
     * 开始红包雨活动，广播直播间用户，开始抢红包
     * @param reqDTO
     * @return
     */
    boolean startRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 领取红包
     * @param reqDTO
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 领取红包之后的处理
     * @param reqDTO
     * @param price
     */
    void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO, Integer price);
}
