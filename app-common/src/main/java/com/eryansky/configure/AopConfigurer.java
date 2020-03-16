package com.eryansky.configure;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
//@ComponentScan({"com.eryansky.modules.**.event"})
//开始异步支持
@EnableAsync
public class AopConfigurer implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        //线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池数量，方法: 返回可用处理器的Java虚拟机的数量。
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors);
        //最大线程数量
        executor.setMaxPoolSize(processors * 5);
        //线程池的队列容量
        executor.setQueueCapacity(processors * 2);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}
