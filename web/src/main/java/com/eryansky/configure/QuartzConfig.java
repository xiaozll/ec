package com.eryansky.configure;

import com.eryansky.core.quartz.QuartJobSchedulingListener;
import com.eryansky.core.quartz.SpringQuartzJobFactory;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter({ QuartzAutoConfiguration.class})
public class QuartzConfig {


    @Bean
    @ConditionalOnSingleCandidate(Scheduler.class)
    public QuartJobSchedulingListener quartJobSchedulingListener(Scheduler scheduler) {
        return new QuartJobSchedulingListener(scheduler);
    }


}