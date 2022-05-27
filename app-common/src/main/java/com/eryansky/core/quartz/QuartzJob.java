package com.eryansky.core.quartz;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
//@Scope("prototype")
public @interface QuartzJob {

	/**
	 * 是否启用
	 * @return
	 */
    boolean enable() default true;

    String name();

    String group() default "DEFAULT_GROUP";

    String cronExp();
}
