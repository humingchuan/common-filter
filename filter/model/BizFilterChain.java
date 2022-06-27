package com.youzan.common.filter.model;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-25 22:44
 * @description
 */
public interface BizFilterChain {
    Object filter(BizFilterContext bizFilterContext);
}
