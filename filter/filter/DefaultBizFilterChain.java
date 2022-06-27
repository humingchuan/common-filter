package com.youzan.common.filter.filter;

import com.youzan.common.filter.model.BizFilter;
import com.youzan.common.filter.model.BizFilterContext;
import com.youzan.common.filter.model.BizFilterChain;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-25 22:57
 * @description
 */
@Data
public class DefaultBizFilterChain implements BizFilterChain {
    private List<BizFilter> filters;
    private int position = 0;

    public void setFilters(List<BizFilter> filters) {
        if (!(filters instanceof RandomAccess)) {
            filters = new ArrayList<>(filters);
        }

        this.filters = filters;
    }

    @SneakyThrows
    @Override
    public Object filter(BizFilterContext bizFilterContext) {
        if (filtersExhausted()) {
            return bizFilterContext.getPjp().proceed();
        }

        return filters.get(position++).filter(bizFilterContext, this);
    }

    private boolean filtersExhausted() {
        return filters == null || filters.size() <= position;
    }
}
