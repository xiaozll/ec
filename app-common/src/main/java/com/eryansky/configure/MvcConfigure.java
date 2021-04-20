package com.eryansky.configure;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.dialect.dialect.ShiroDialect;
import com.eryansky.core.security.interceptor.AuthorityInterceptor;
import com.eryansky.core.web.interceptor.LogInterceptor;
import com.eryansky.core.web.interceptor.MobileInterceptor;
import com.eryansky.modules.disk.extend.DISKManager;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.utils.AppConstants;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.*;

@Configuration
public class MvcConfigure implements WebMvcConfigurer {


   @Autowired
   private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler(
              "/webjars/**",
              "/img/**",
              "/css/**",
              "/js/**")
              .addResourceLocations(
                      "classpath:/webjars/",
                      "classpath:/META-INF/resources/webjars/",
                      "classpath:/static/img/",
                      "classpath:/static/css/",
                      "classpath:/static/js/")
              .resourceChain(false);
   }


   /**
    * 配置拦截器
    * @param registry
    */
   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new LogInterceptor(requestMappingHandlerAdapter))
              .addPathPatterns("/**")
              .excludePathPatterns("/static/**")
              .order(Ordered.HIGHEST_PRECEDENCE + 100);
      AuthorityInterceptor authorityInterceptor = new AuthorityInterceptor();
      authorityInterceptor.setRedirectURL("/jump.jsp");
      registry.addInterceptor(authorityInterceptor).addPathPatterns("/**")
              .excludePathPatterns(Lists.newArrayList("/jump.jsp","/static/**","favicon**","/userfiles/**","/servlet/**","/error/**","/api/**","/rest/**"))
              .order(Ordered.HIGHEST_PRECEDENCE + 200);
      registry.addInterceptor(new MobileInterceptor())
              .addPathPatterns("/**")
              .order(Ordered.HIGHEST_PRECEDENCE + 300);
   }

   /**
    * 跨域配置
    * @param registry
    */
   @Override
   public void addCorsMappings(CorsRegistry registry) {
//      registry.addMapping("/api/**");
      registry.addMapping("/**")
//              .allowedOrigins("*")
              .allowedOriginPatterns("*")
              .allowedMethods("GET","POST","OPTIONS","PUT", "DELETE")
              .allowedHeaders("Origin","Content-Type","X-Amz-Date","Authorization","X-Api-Key","X-Amz-Security-Token")
              .allowCredentials(true);
   }

   /**
    * Json解析
    * @return
    */
   @Bean
   public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
      final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
      //设置日期格式
      JsonMapper objectMapper = new JsonMapper();
      mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
      /**
       * 序列换成json时,将所有的long变成string
       * 因为js中得数字类型不能包含所有的java long值
       */
//      SimpleModule simpleModule = new SimpleModule();
//      simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//      simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//      objectMapper.registerModule(simpleModule);
      //设置中文编码格式
      List<MediaType> list = new ArrayList<MediaType>();
      list.add(MediaType.APPLICATION_JSON_UTF8);
      list.add(MediaType.TEXT_PLAIN);
      list.add(MediaType.TEXT_HTML);
      mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
      return mappingJackson2HttpMessageConverter;
   }


   @Bean
   public IFileManager fileManager() {
      final IFileManager bean = new DISKManager();
      return bean;
   }

//
//   @Bean
//   public LayoutDialect layoutDialect() {
//      return new LayoutDialect();
//   }

   @Bean
   public ShiroDialect shiroDialect() {
      return new ShiroDialect();
   }

}