package top.mylove7.live.living.provider.room.dao.po;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.elasticsearch.core.mapping.PropertyValueConverter;
import top.mylove7.live.common.interfaces.error.BizErrorException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@AutoConfiguration
@Slf4j
public class CentralstoreLocalDateTimeConverter implements PropertyValueConverter {
    // 使用系统默认时区
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    @Override
    public Object write(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            //2025-01-06T20:57:57.90+08:00
            //2025-03-03T11:59:57.00
            return DateUtil.format(localDateTime, "yyyy-MM-dd'T'HH:mm:ss.SSS");

        }
        if (value instanceof LocalDate localDate){
            return DateUtil.format(localDate.atStartOfDay(), "yyyy-MM-dd");
        }
        throw new BizErrorException("日期错误");
    }

    @Override
    public Object read(Object value) {
        if (value instanceof Long timestamp) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZONE_ID);
            log.info("将时间戳 [{}] 转换为 LocalDateTime [{}]", timestamp, localDateTime);
            return localDateTime;
        } else {
            String errorMessage = String.format("无法将值 '值: [%s], 类型: [%s] 解析为 LocalDateTime", value, value.getClass().getName());
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

}