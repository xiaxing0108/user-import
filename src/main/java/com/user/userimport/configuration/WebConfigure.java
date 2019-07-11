package com.user.userimport.configuration;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class WebConfigure {

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,   //是否格式化输出，默认为false
                SerializerFeature.DisableCircularReferenceDetect,   //禁止循环引用，即出现$.data[0]
                SerializerFeature.WriteMapNullValue,   //Map字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty,   //List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullStringAsEmpty   //字符类型字段如果为null,输出为”“,而非null
        );
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");   //统一配置日期格式
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastConverter;
        return new HttpMessageConverters(converter);
    }


}
