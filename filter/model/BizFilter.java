package com.youzan.common.filter.model;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:57
 * @description
 */
public interface BizFilter {
    /**
     * 对方法进行
     *
     * @param bizFilterContext
     * @param chain
     * @return
     */
    Object filter(BizFilterContext bizFilterContext, BizFilterChain chain);
}
