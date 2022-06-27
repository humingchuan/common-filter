package com.youzan.common.filter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:13
 * @description
 */
@Configuration
@ComponentScan("com.youzan.common.filter")
public class BizFilterAutoConfiguration {
    @Bean
    @ConfigurationProperties("yz.bizFilter")
    public BizFilterConfigs globalBizFilterConfig() {
        return new BizFilterConfigs();
    }

}
