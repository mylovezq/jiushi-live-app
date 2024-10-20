package top.mylove7.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.mylove7.live.bank.provider.dao.maper.ICurrencyTradeMapper;
import top.mylove7.live.bank.provider.dao.po.CurrencyTradePO;
import top.mylove7.live.bank.provider.service.ICurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.mylove7.live.common.interfaces.error.BizErrorException;

/**
 * @Author jiushi
 *
 * @Description
 */
@Service
@Slf4j
public class JiushiCurrencyTradeServiceImpl implements ICurrencyTradeService {


    @Resource
    private ICurrencyTradeMapper currencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            CurrencyTradePO tradePO = new CurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            currencyTradeMapper.insert(tradePO);
            return true;
        } catch (Exception e) {
            log.error("[JiushiCurrencyTradeServiceImpl] insert error is:", e);
            throw new BizErrorException("记录流水失败");
        }
    }
}
