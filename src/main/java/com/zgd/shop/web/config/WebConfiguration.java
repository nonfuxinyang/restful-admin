package com.zgd.shop.web.config;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * WebConfiguration
 *
 * @author zgd
 * @date 2019/7/17 19:49
 */
@Configuration
public class WebConfiguration  {

  @Bean
  public HttpMessageConverters fastJsonHttpMessageConverters() {
    //定义一个converter 转化消息对象
    FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
    //添加fastjson的配置信息，例如格式
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
//    fastJsonConfig.setSerializeFilters(new SerializeFilter() {
//
//    });
    //将配置信息添加到converter中
    fastConverter.setFastJsonConfig(fastJsonConfig);
    HttpMessageConverter<?> converter = fastConverter;
    return new HttpMessageConverters(converter);
  }
}
