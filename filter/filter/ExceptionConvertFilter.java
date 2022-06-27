package com.youzan.common.filter.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.youzan.api.common.response.BaseResult;
import com.youzan.api.common.response.ListResult;
import com.youzan.api.common.response.PaginatorResult;
import com.youzan.api.common.response.PlainResult;
import com.youzan.ebiz.showcase.common.exception.BizException;
import com.youzan.common.filter.model.BizFilterChain;
import com.youzan.common.filter.model.BizFilterContext;
import com.youzan.common.filter.model.BizFilterSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-27 11:42
 * @description
 */
@Component
@Order(1)
@Slf4j
public class ExceptionConvertFilter extends BizFilterSupport {
    @Override
    public Object filter(BizFilterContext bizFilterContext, BizFilterChain chain) {
        try {
            Object result = chain.filter(bizFilterContext);
            //record(result, bizFilterContext, System.currentTimeMillis() - beginTime);
            return result;
        } catch (Exception e) {
            Class<?> returnType = bizFilterContext.getReturnType();
            if (PlainResult.class.isAssignableFrom(returnType)) {
                PlainResult<?> result = new PlainResult<>();
                fillErrorInfo(bizFilterContext.getPjp(), e, result);
                return result;
            }

            if (PaginatorResult.class.isAssignableFrom(returnType)) {
                PaginatorResult<Object> plainResult = new PaginatorResult<>();
                fillErrorInfo(bizFilterContext.getPjp(), e, plainResult);
                return plainResult;
            }

            if (ListResult.class.isAssignableFrom(returnType)) {
                PaginatorResult<Object> plainResult = new PaginatorResult<>();
                fillErrorInfo(bizFilterContext.getPjp(), e, plainResult);
                return plainResult;
            }

            //record(e, bizFilterContext, System.currentTimeMillis() - beginTime);
            throw e;
        }
    }

    private void record(Object result, BizFilterContext bizFilterContext, long cost) {
        try {
            if (result instanceof BaseResult) {
                BaseResult baseResult = (BaseResult) result;
                if (((BaseResult) result).isSuccess()) {
                    log.warn(MessageFormat.format("[BizService][SUCCESS][{0}][{1}][{2}{3}],\nargs:{4},\nresult:{5}",
                            baseResult.getCode(),
                            baseResult.getMessage(),
                            bizFilterContext.getMethodAlias(),
                            cost,
                            JSON.toJSONString(bizFilterContext.getArgs(), SerializerFeature.PrettyFormat),
                            JSON.toJSONString(result)));
                } else {
                    log.warn(MessageFormat.format("[BizService][FAIL][{0}][{1}][{2}{3}],\nargs:{4},\nresult:{5}",
                            baseResult.getCode(),
                            baseResult.getMessage(),
                            bizFilterContext.getMethodAlias(),
                            cost,
                            JSON.toJSONString(bizFilterContext.getArgs(), SerializerFeature.PrettyFormat),
                            JSON.toJSONString(result)));
                }
            }
            if (result instanceof Exception) {
                log.warn(MessageFormat.format("[BizService][FAIL][{0}][{1}][{2}{3}],\nargs:{4},\nresult:{5}",
                        -1,
                        ((Exception) result).getMessage(),
                        bizFilterContext.getMethodAlias(),
                        cost,
                        JSON.toJSONString(bizFilterContext.getArgs(), SerializerFeature.PrettyFormat),
                        ExceptionUtils.getFullStackTrace(((Exception) result))));
            }
        } catch (Exception e) {
            log.warn("fail to record ", e);
        }
    }

    private void fillErrorInfo(ProceedingJoinPoint pjp,
                               Exception e,
                               BaseResult plainResult) {
        plainResult.setSuccess(false);
        int code = -1;
        String message = e.getMessage();
        if (e instanceof BizException) {
            code = ((BizException) e).getErrorCode().getCode();
        }

        plainResult.setCode(code);
        plainResult.setMessage(message);

        log.warn(MessageFormat.format("[BizService][FAIL][{0}][{1}][{2}],\nargs:{3},\nerror:{4}",
                code,
                message,
                pjp.getSignature().toShortString(),
                JSON.toJSONString(pjp.getArgs(), SerializerFeature.PrettyFormat),
                ExceptionUtils.getFullStackTrace(e)));
    }
}
