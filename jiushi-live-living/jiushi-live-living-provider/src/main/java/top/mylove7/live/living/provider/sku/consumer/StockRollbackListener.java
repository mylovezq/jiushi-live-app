package top.mylove7.live.living.provider.sku.consumer;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import top.mylove7.live.common.interfaces.topic.SkuProviderTopicNames;
import top.mylove7.live.living.interfaces.sku.dto.RockBackInfoDTO;
import top.mylove7.live.living.provider.sku.service.ISkuStockInfoService;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = SkuProviderTopicNames.ROLL_BACK_STOCK,
        consumerGroup = "${spring.application.name}_StockRollbackListener",
        messageModel = MessageModel.CLUSTERING)
public class StockRollbackListener implements RocketMQListener<RockBackInfoDTO> {

    @Resource
    private ISkuStockInfoService skuStockInfoService;

    @Override
    public void onMessage(RockBackInfoDTO rockBackInfoDTO) {
        skuStockInfoService.stockRollbackHandler(rockBackInfoDTO);
    }


}
