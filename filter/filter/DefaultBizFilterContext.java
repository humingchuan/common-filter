package com.youzan.common.filter.filter;

import com.youzan.common.filter.model.BizFilterContext;
import com.youzan.common.filter.model.BizFilterChain;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:44
 * @description
 */

@Data
public class DefaultBizFilterContext implements BizFilterContext {
    private String scenario;

    private Method method;
    private ProceedingJoinPoint pjp;

    private Object result;
    private Throwable error;

    private BizFilterChain bizFilterChain;

    private boolean recordInputs = true;

    private boolean recordOutputs = true;

    private boolean recordException = true;

    private boolean recordCost = true;

    private String methodAlias;

    private String serviceAlias;

    private boolean reportMonitor = false;

    private String[] extConfig = new String[0];

    @Override
    public String getMethodAlias() {
        if (StringUtils.isBlank(methodAlias)) {
            methodAlias = method.getDeclaringClass().getCanonicalName() + "." + method.getName();
        }

        return methodAlias;
    }

    @Override
    public Object[] getArgs() {
        return pjp.getArgs();
    }

    @Override
    public Class<?> getReturnType() {
        return method.getReturnType();
    }
}
