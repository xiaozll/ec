package com.eryansky.core.quartz;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
//@Scope("prototype")
public @interface QuartzJob {

    String DEFAULT_INSTANCE_NAME = "AUTO";
	/**
	 * 是否启用
	 * @return
	 */
    boolean enable() default true;

    /**
     * 执行实例名称
     * @return
     */
    String instanceName() default DEFAULT_INSTANCE_NAME;

    String name();

    String group() default "DEFAULT_GROUP";

    String cronExp();
}
