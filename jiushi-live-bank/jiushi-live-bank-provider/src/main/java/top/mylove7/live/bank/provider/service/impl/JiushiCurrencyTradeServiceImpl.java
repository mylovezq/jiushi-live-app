package top.mylove7.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import top.mylove7.live.bank.provider.dao.maper.IQiyuCurrencyTradeMapper;
import top.mylove7.live.bank.provider.dao.po.CurrencyTradePO;
import top.mylove7.live.bank.provider.service.IQiyuCurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
public class JiushiCurrencyTradeServiceImpl implements IQiyuCurrencyTradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiushiCurrencyTradeServiceImpl.class);

    @Resource
    private IQiyuCurrencyTradeMapper qiyuCurrencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            CurrencyTradePO tradePO = new CurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            qiyuCurrencyTradeMapper.insert(tradePO);
            return true;
        } catch (Exception e) {
            LOGGER.error("[JiushiCurrencyTradeServiceImpl] insert error is:", e);
        }
        return false;
    }
}
