package com.eryansky.configure;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.dialect.dialect.ShiroDialect;
import com.eryansky.core.security.interceptor.AuthorityInterceptor;
import com.eryansky.core.web.interceptor.LogInterceptor;
import com.eryansky.core.web.interceptor.MobileInterceptor;
import com.eryansky.modules.disk.extend.DISKManager;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.*;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


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
      registry.addInterceptor(new LogInterceptor(requestMappingHandlerAdapter)).addPathPatterns("/**").excludePathPatterns("/static/**");
      registry.addInterceptor(new AuthorityInterceptor().setRedirectURL("/jump.jsp")).addPathPatterns("/**")
              .excludePathPatterns(Arrays.asList(new String[]{"/jump.jsp","/static/**","favicon**","/userfiles/**","/servlet/**","/error/**","/fop/**"}));
      registry.addInterceptor(new MobileInterceptor()).addPathPatterns(AppConstants.getMobilePath()+"/*");
   }

   /**
    * 跨域配置
    * @param registry
    */
   @Override
   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/api/**");
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
      //设置中文编码格式
      List<MediaType> list = new ArrayList<MediaType>();
      list.add(MediaType.APPLICATION_JSON_UTF8);
      list.add(MediaType.TEXT_PLAIN);
      list.add(MediaType.TEXT_HTML);
      mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
      return mappingJackson2HttpMessageConverter;
   }


   @Bean
   public IFileManager getFileManager() {
      final IFileManager iFileManager = new DISKManager();
      return iFileManager;
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