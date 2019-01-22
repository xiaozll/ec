package com.eryansky.configure;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.security.interceptor.AuthorityInterceptor;
import com.eryansky.core.web.interceptor.LogInterceptor;
import com.eryansky.core.web.interceptor.MobileInterceptor;
import com.eryansky.modules.disk.extend.DISKManager;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

   @Value("${file.dir}")
   String fileDir;

   @Autowired
   private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler(
              "/common/**",
              "/webjars/**",
              "/img/**",
              "/css/**",
              "/js/**")
              .addResourceLocations(
                      "classpath:/webapp/",
                      "classpath:/webjars/",
                      "classpath:/META-INF/resources/webjars/",
                      "classpath:/static/img/",
                      "classpath:/static/css/",
                      "classpath:/static/js/",
                      "file:"+fileDir)
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
              .excludePathPatterns(Arrays.asList(new String[]{"/jump.jsp","/static/**","favicon**","/userfiles/**","/servlet/**","/error/**"}));
      registry.addInterceptor(new MobileInterceptor()).addPathPatterns(AppConstants.getMobilePath()+"/*");
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
//      list.add(MediaType.TEXT_PLAIN);
      mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
      return mappingJackson2HttpMessageConverter;
   }

   /**
    * 附件上传大小配置
    * @return
    */
   @Bean
   public CommonsMultipartResolver commonsMultipartResolver() {
      final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
      commonsMultipartResolver.setMaxUploadSize(Long.valueOf(AppConstants.getAppConfig("web.maxUploadSize","-1")));
      return commonsMultipartResolver;
   }

   @Bean
   public IFileManager getFileManager() {
      final IFileManager iFileManager = new DISKManager();
      return iFileManager;
   }
}