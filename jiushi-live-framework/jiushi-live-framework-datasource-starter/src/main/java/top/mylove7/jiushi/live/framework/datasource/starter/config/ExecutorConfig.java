package top.mylove7.jiushi.live.framework.datasource.starter.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @Author: Colin Yin
 * @Date: 2022/3/21
 */
@Configuration
public class ExecutorConfig {

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    private static int getCupExecutorCorePoolSize() {
        return Math.max(CORES + 1, 10);
    }

    private static int getIoExecutorCorePoolSize() {
        return Math.max(CORES * 2, 10);
    }
    @Primary
    @Bean("ioExecutor")
    public ExecutorService getIoExecutorService() {
        int maximumPoolSize = getIoExecutorCorePoolSize() * 6;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getIoExecutorCorePoolSize(), maximumPoolSize,
                600L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(5000),
                new ThreadFactoryBuilder().setNameFormat("io-pool-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.prestartAllCoreThreads();
        return TtlExecutors.getTtlExecutorService(executor);
    }

    /**
     * 处理大量的CPU计算任务 N+1
     */
    @Bean("cupExecutor")
    public ExecutorService getCpuExecutorService() {
        int maximumPoolSize = getCupExecutorCorePoolSize() * 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getCupExecutorCorePoolSize(), maximumPoolSize,
                300L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                new ThreadFactoryBuilder().setNameFormat("cpu-pool-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.prestartAllCoreThreads();
        return TtlExecutors.getTtlExecutorService(executor);
    }
}
