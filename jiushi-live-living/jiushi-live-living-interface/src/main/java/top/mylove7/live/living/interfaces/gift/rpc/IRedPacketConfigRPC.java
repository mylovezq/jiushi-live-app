package top.mylove7.live.living.interfaces.gift.rpc;

import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigReqDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketConfigRespDTO;
import top.mylove7.live.living.interfaces.gift.dto.RedPacketReceiveDTO;

public interface IRedPacketConfigRPC {
    /**
     * 根据主播id查询是否有红包雨配置特权
     * @param authorId
     * @return
     */
    RedPacketConfigRespDTO queryByAuthorId(Long authorId);

    /**
     * 新增配置
     * @param redPacketConfigRespDTO
     * @return
     */
    boolean addOne(RedPacketConfigRespDTO redPacketConfigRespDTO);

    /**
     * 更新配置
     * @param redPacketConfigRespDTO
     * @return
     */
    boolean updateById(RedPacketConfigRespDTO redPacketConfigRespDTO);


    /**
     * 生成红包雨数据
     * @param authorId
     * @return
     */
    boolean prepareRedPacket(Long authorId);


    /**
     * 领取红包
     * @param reqDTO
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 开始红包雨活动，广播直播间用户，开始抢红包
     *
     * @param reqDTO
     * @return
     */
    boolean startRedPacket(RedPacketConfigReqDTO reqDTO);
}
