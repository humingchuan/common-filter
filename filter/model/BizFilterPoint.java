package com.youzan.common.filter.model;

import java.lang.annotation.*;

/**
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:51
 * @description
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BizFilterPoint {
    boolean recordRequest() default false;

    boolean recordResponse() default false;

    boolean recordException() default false;

    boolean recordCost() default false;

    String alias() default "";

    String scenario() default "";

    boolean reportMonitor() default false;

    String[] extConfig() default {};
}
