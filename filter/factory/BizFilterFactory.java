package com.youzan.common.filter.factory;

import com.youzan.common.filter.config.BizFilterConfig;
import com.youzan.common.filter.model.BizFilter;
import com.youzan.common.filter.model.BizFilterContext;
import com.youzan.common.filter.model.BizFilterPoint;
import com.youzan.common.filter.config.BizFilterConfigs;
import com.youzan.common.filter.filter.DefaultBizFilterChain;
import com.youzan.common.filter.filter.DefaultBizFilterContext;
import com.youzan.common.filter.model.BizFilterChain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:23
 * @description
 */
@Component
@Slf4j
public class BizFilterFactory implements ApplicationContextAware {
    @Resource
    private BizFilterConfigs bizFilterConfigs;

    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private List<BizFilter> bizFilters;

    @Nonnull
    public BizFilterContext generateContext(ProceedingJoinPoint pjp) {
        Method method = getMethod(pjp);

        try {
            Class<?> declaringClass = method.getDeclaringClass();

            BizFilterPoint serviceCfg = declaringClass.getAnnotation(BizFilterPoint.class);
            BizFilterPoint methodCfg = method.getAnnotation(BizFilterPoint.class);

            if (methodCfg == null && serviceCfg == null) {
                return mockContext(pjp);
            }

            return extractConfig(serviceCfg, methodCfg, pjp, method);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Fail to extract config ,error is :", e);
            } else {
                log.warn("Fail to extract config ,because of :{}", e.getMessage());
            }
            return mockContext(pjp);
        }
    }

    private Method getMethod(ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod();
    }

    private BizFilterContext mockContext(ProceedingJoinPoint pjp) {
        DefaultBizFilterContext context = new DefaultBizFilterContext();
        context.setMethod(getMethod(pjp));
        context.setBizFilterChain(new BizFilterChain() {
            @SneakyThrows
            @Override
            public Object filter(BizFilterContext bizFilterContext) {
                return pjp.proceed();
            }
        });

        return context;
    }


    @Nonnull
    private BizFilterContext extractConfig(BizFilterPoint serviceCfg,
                                           BizFilterPoint methodCfg,
                                           ProceedingJoinPoint pjp,
                                           Method method) {
        DefaultBizFilterContext filterContext = new DefaultBizFilterContext();
        filterContext.setMethod(method);
        filterContext.setPjp(pjp);

        // 1. 注入Service维度的配置，作为兜底配置
        if (serviceCfg != null) {
            fillConfig(serviceCfg, filterContext);
            String alias = serviceCfg.alias();
            if (StringUtils.isNotBlank(alias)) {
                filterContext.setServiceAlias(serviceCfg.alias());
            }
        }

        // 2. 注入Method维度的配置,非空值会覆盖现有值
        if (methodCfg != null) {
            fillConfig(methodCfg, filterContext);
            String alias = methodCfg.alias();
            if (StringUtils.isNotBlank(alias)) {
                filterContext.setMethodAlias(alias);
            }
        }

        // 3. 注入自定义场景的配置,非空值会覆盖现有值
        fillScenarioConfig(filterContext);

        return filterContext;
    }

    private void fillScenarioConfig(DefaultBizFilterContext filterConfig) {
        BizFilterConfig scenarioConfig = bizFilterConfigs.getBizFilterConfig(filterConfig.getScenario());
        fillConfig(scenarioConfig, filterConfig);
        fillBizFilterChain(filterConfig, scenarioConfig);
    }

    private void fillBizFilterChain(DefaultBizFilterContext context, BizFilterConfig scenarioConfig) {
        BizFilterChain bizFilterChain = generateFilterChain(scenarioConfig);
        context.setBizFilterChain(bizFilterChain);
    }

    private DefaultBizFilterChain generateFilterChain(BizFilterConfig scenarioConfig) {
        DefaultBizFilterChain filterChain = new DefaultBizFilterChain();
        List<BizFilter> filterList = getFilters(scenarioConfig);
        filterChain.setFilters(filterList);
        return filterChain;
    }

    private List<BizFilter> getFilters(BizFilterConfig scenarioConfig) {
        List<String> filters = scenarioConfig.getBizFilters();
        List<BizFilter> filterList;
        filterList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(filters)) {
            filterList = filters.stream()
                                .map(this::getBizFilterByName)
                                .collect(Collectors.toList());
        } else {
            filterList = bizFilters;
        }
        return filterList;
    }

    private BizFilter getBizFilterByName(String bizFilterName) {
        return applicationContext.getBean(bizFilterName, BizFilter.class);
    }

    private void fillConfig(BizFilterConfig scenarioConfig, DefaultBizFilterContext bizFilterContext) {

    }

    private void fillConfig(BizFilterPoint serviceCfg, DefaultBizFilterContext bizFilter) {
        bizFilter.setReportMonitor(serviceCfg.reportMonitor());
        bizFilter.setRecordInputs(serviceCfg.recordRequest());
        bizFilter.setRecordOutputs(serviceCfg.recordResponse());
        bizFilter.setRecordException(serviceCfg.recordException());
        bizFilter.setRecordCost(serviceCfg.recordCost());
        doIfNotNull(serviceCfg.scenario(), bizFilter::setScenario);
        doIfNotNull(serviceCfg.extConfig(), bizFilter::setExtConfig);
    }

    private <T> void doIfNotNull(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
