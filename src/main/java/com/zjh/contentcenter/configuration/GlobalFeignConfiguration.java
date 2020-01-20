package com.zjh.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName UserCenterFeignConfiguration
 * @Author zhaojh
 * @Date 2020/1/2 10:58
 * @Version 1.0
 * @Description //TODO
 * Feign的配置类
 * 这个类别加@Configuration注解了，否则必须挪到@ComponentScan能扫描到的包以外
 * 默认情况下就是启动包以外
 **/
public class GlobalFeignConfiguration {
    @Bean
    public Logger.Level level() {
        //让feign打印所有请求的详细信息
        return Logger.Level.FULL;
    }
}
