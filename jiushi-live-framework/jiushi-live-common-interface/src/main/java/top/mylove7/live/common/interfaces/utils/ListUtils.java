package top.mylove7.live.common.interfaces.utils;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListUtils {

    /**
     * 生成红包金额(二倍随机法)
     *
     * @param totalCount
     * @param totalPrice
     * @return
     */
    public static List<Long> createRedPacketPriceList(Integer totalCount, Long totalPrice) {
        List<Long> redPacketPriceList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            //最后一个红包
            if (totalCount == i + 1) {
                redPacketPriceList.add(totalPrice);
                break;
            }
            long maxLimit = (totalPrice / (totalCount - i)) * 2;
            long currentPrice = ThreadLocalRandom.current().nextLong(1, maxLimit);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
    }


    /**
     * 将一个List集合拆解为多个子List集合
     *
     * @param list
     * @param subNum
     * @return
     * @param <T>
     */
    public static <T> List<List<T>> splitList(List<T> list, int subNum) {
        Assert.isTrue(subNum > 0, "集合分割集合必须大于0");
        return IntStream.range(0, (list.size() + subNum - 1) / subNum)
                .mapToObj(i -> list.subList(i * subNum, Math.min((i + 1) * subNum, list.size())))
                .collect(Collectors.toList());
    }

}
