package com.zjh.contentcenter.configuration;

import com.zjh.ribbonconfiguration.RibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName UserCenterRibbonConfiguration
 * @Author zhaojh
 * @Date 2019/12/24 16:00
 * @Version 1.0
 * @Description //TODO
 **/
@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {

}


