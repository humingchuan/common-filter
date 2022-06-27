package com.youzan.common.filter.model;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-25 22:47
 * @description
 */
public class BizFilterSupport implements BizFilter {
    @Override
    public Object filter(BizFilterContext bizFilterContext, BizFilterChain chain) {
        return chain.filter(bizFilterContext);
    }
}
