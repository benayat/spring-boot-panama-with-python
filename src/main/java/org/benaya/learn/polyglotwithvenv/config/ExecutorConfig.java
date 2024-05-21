package org.benaya.learn.polyglotwithvenv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorConfig {

    @Bean(name = "blockingExecutor", destroyMethod = "shutdown")
    public ScheduledExecutorService blockingExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }


}
