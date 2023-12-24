package com.seng.conversion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfiguration {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // number of threads in the pool
        executor.setMaxPoolSize(8); // maximum number of threads
        executor.setQueueCapacity(100); // queue capacity for tasks
        executor.initialize();
        return executor;
    }
}
