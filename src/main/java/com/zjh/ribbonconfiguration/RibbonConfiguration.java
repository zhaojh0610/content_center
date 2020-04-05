package com.zjh.ribbonconfiguration;

import com.netflix.loadbalancer.IRule;
import com.zjh.contentcenter.configuration.NacosSameClusterWeightedRule;
import com.zjh.contentcenter.configuration.NacosWeightedRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RibbonConfiguration
 * @Author zhaojh
 * @Date 2019/12/25 20:03
 * @Version 1.0
 * @Description //TODO
 **/
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule() {
        return new NacosSameClusterWeightedRule();
    }
}
