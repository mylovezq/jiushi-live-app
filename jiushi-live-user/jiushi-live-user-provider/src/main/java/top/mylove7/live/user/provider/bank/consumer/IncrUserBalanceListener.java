package top.mylove7.live.user.provider.bank.consumer;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.mylove7.live.common.interfaces.topic.BalanceChangeTopic;
import top.mylove7.live.user.interfaces.bank.dto.BalanceMqDto;
import top.mylove7.live.user.provider.bank.service.IMyCurrencyAccountService;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = BalanceChangeTopic.INCR_BALANCE,
        consumerGroup = "${spring.application.name}_IncrUserBalanceListener",
        messageModel = MessageModel.CLUSTERING)
public class IncrUserBalanceListener implements RocketMQListener<BalanceMqDto> {

    @Resource
    private IMyCurrencyAccountService myCurrencyAccountService;

    @Override
    public void onMessage(BalanceMqDto balanceMqDto) {
        log.info("开始新增账户balanceMqDto{}", balanceMqDto);
        myCurrencyAccountService.incr(balanceMqDto);
    }
}
