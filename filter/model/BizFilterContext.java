package com.youzan.common.filter.model;

import org.aspectj.lang.ProceedingJoinPoint;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:44
 * @description
 */
public interface BizFilterContext {
    String getScenario();

    Method getMethod();

    ProceedingJoinPoint getPjp();

    Object getResult();

    Throwable getError();

    void setError(Throwable error);

    @Nonnull
    BizFilterChain getBizFilterChain();

    boolean isRecordInputs();

    boolean isRecordOutputs();

    boolean isRecordException();

    boolean isRecordCost();

    String getMethodAlias();

    String getServiceAlias();

    boolean isReportMonitor();

    String[] getExtConfig();

    void setResult(Object result);

    void setRecordCost(boolean recordCost);

    Object[] getArgs();

    Class<?> getReturnType();
}
