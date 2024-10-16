package top.mylove7.live.common.interfaces.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 线程池配置
 *
 * @Author:
 * @Date: 2022/3/21
 */
@Slf4j
public class ExecutorConfig {

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    private static int getCupExecutorCorePoolSize() {
        return Math.max(CORES + 1, 10);
    }

    private static int getIoExecutorCorePoolSize() {
        return Math.max(CORES * 2, 10);
    }

    public static final ExecutorService IO_EXECUTOR = getIoExecutorService();
    public static final ExecutorService CUP_EXECUTOR = getCpuExecutorService();

    public static ExecutorService getIoExecutorService() {
        int maximumPoolSize = getIoExecutorCorePoolSize() * 6;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getIoExecutorCorePoolSize(), maximumPoolSize,
                600L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50000),
                new ThreadFactoryBuilder()
                        .setNamePrefix("io-thread-pool-") // 设置线程名称前缀
                        .setDaemon(true).build(),// 设置是否为守护线程,
                new ThreadPoolExecutor.CallerRunsPolicy() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        log.warn("触发拒绝策略 由调用者线程执行");
                        super.rejectedExecution(r, e);
                    }
                }
        );

        executor.prestartAllCoreThreads();
        return TtlExecutors.getTtlExecutorService(executor);
    }

    /**
     * 处理大量的CPU计算任务 N+1
     */
    public static ExecutorService getCpuExecutorService() {
        int maximumPoolSize = getCupExecutorCorePoolSize() * 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(getCupExecutorCorePoolSize(), maximumPoolSize,
                300L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20000),
                new ThreadFactoryBuilder()
                        .setNamePrefix("cpu-thread-pool-") // 设置线程名称前缀
                        .setDaemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.prestartAllCoreThreads();
        return TtlExecutors.getTtlExecutorService(executor);
    }
}
