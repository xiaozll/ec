package com.eryansky.j2cache.autoconfigure;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;
import com.eryansky.j2cache.J2CacheBuilder;
import com.eryansky.j2cache.cache.support.util.SpringJ2CacheConfigUtil;
import com.eryansky.j2cache.cache.support.util.SpringUtil;
/**
 * 启动入口
 * @author zhangsaizz
 *
 */
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheConfig.class})
@Configuration
@PropertySource(value = "${j2cache.config-location}", encoding = "UTF-8", ignoreResourceNotFound = true)
public class J2CacheAutoConfiguration {
	
    @Autowired
    private StandardEnvironment standardEnvironment;

    @Bean
    public com.eryansky.j2cache.J2CacheConfig j2CacheConfig() throws IOException{
    	com.eryansky.j2cache.J2CacheConfig cacheConfig = new com.eryansky.j2cache.J2CacheConfig();
    	cacheConfig = SpringJ2CacheConfigUtil.initFromConfig(standardEnvironment);
    	return cacheConfig;
    }
    
    @Bean
    @DependsOn({"springUtil","j2CacheConfig"})
    public CacheChannel cacheChannel(com.eryansky.j2cache.J2CacheConfig j2CacheConfig) throws IOException {
    	J2CacheBuilder builder = J2CacheBuilder.init(j2CacheConfig);
        return builder.getChannel();
    }

    @Bean
    public SpringUtil springUtil() {
    	return new SpringUtil();
    }

}
