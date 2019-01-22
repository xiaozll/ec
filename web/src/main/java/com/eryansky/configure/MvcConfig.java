package com.eryansky.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

   @Value("${file.dir}")
   String fileDir;

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
                      "classpath:/static/js/",
                      "file:"+fileDir)
              .resourceChain(false);
   }
}