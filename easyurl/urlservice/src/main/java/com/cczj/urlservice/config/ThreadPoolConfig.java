package com.cczj.urlservice.config;

import com.cczj.framework.config.MDCContextDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    private static final int SIZE = Runtime.getRuntime().availableProcessors();

    @Bean
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        int coreSize = SIZE;
        log.info("初始化主线程池,core pool size:{}", coreSize);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(coreSize);
        // 设置最大线程数
        executor.setMaxPoolSize(coreSize * 2);
        // 设置队列容量
        executor.setQueueCapacity(1000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("task-job-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池  全局线程池不能关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setTaskDecorator(new MDCContextDecorator());
        return executor;
    }


}
