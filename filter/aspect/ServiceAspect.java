package com.youzan.common.filter.aspect;

import com.youzan.common.filter.factory.BizFilterFactory;
import com.youzan.common.filter.model.BizFilterContext;
import com.youzan.common.filter.model.BizFilterChain;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:07
 * @description
 */
@Aspect
@Component
public class ServiceAspect {
    @Resource
    private BizFilterFactory factory;

    @Pointcut("@within(com.youzan.common.filter.model.BizFilterPoint)")
    private void service() {
    }

    @Pointcut("@annotation(com.youzan.common.filter.model.BizFilterPoint)")
    private void method() {
    }

    @Around("service() || method()")
    public Object process(ProceedingJoinPoint pjp) throws Throwable {
        BizFilterContext context = factory.generateContext(pjp);
        BizFilterChain bizFilterChain = context.getBizFilterChain();
        return bizFilterChain.filter(context);
    }
}