package com.eryansky.core.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API访问频率限制
 * @author Eryan
 * @date : 2021-11-05
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiterApi {

	/**
	 * 全局范围或session范围
	 * @return
	 */
	boolean scopeAll() default false;
	/**
	 * 缓存key
	 *
	 * @return
	 */
	String region() default "lock_limit_key_timeout_default";

	/**
	 * 访问次数
	 * @return
	 */
	int frequency() default 500;

	/**
	 * 业务KEY
	 * @return
	 */
	String paramKey() default "CDPathSta";
	

	/**
	 * 计时周期(无效配置，请在缓存中配置caffeine.properties，对应region) 单位：秒
	 * @return
	 */
	@Deprecated
	long timeout() default 60;
		
}
