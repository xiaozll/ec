package com.eryansky.configure;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
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

    private static Logger logger = LoggerFactory.getLogger(AopConfigurer.class);

//    @Value("${thread.pool.corePoolSize:10}")
//    private int corePoolSize;
//
//    @Value("${thread.pool.maxPoolSize:20}")
//    private int maxPoolSize;
//
//    @Value("${thread.pool.keepAliveSeconds:60}")
//    private int keepAliveSeconds;
//
//    @Value("${thread.pool.queueCapacity:1024}")
//    private int queueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        //线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池数量，方法: 返回可用处理器的Java虚拟机的数量。
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 5);//最大线程数量
        executor.setQueueCapacity(processors * 100);//线程池的队列容量
        executor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor exe) -> {
            logger.error("当前任务线程池队列已满. {} {} {}",executor.getCorePoolSize(),executor.getMaxPoolSize(),executor.getActiveCount());
        });
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> logger.error("线程池执行任务发生未知异常,{} {}:{}",method.getName(),throwable.getMessage(), throwable);
    }

}
