package com.eryansky.core.aop.annotation;

/**
 * 排队(串行执行)
 */
public @interface QueuePoll {
    /**
     * 缓存key
     *
     * @return
     */
    String region() default "queue_poll";

    /**
     * 队列名称
     * @return
     */
    String value() default "";
}
