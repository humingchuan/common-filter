package com.youzan.common.filter.config;

import org.springframework.context.annotation.Import;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-27 19:20
 * @description
 */
@Import(BizFilterAutoConfiguration.class)
public @interface EnableBizFilter {
}
