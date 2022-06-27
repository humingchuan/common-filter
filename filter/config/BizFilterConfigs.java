package com.youzan.common.filter.config;

import lombok.Data;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 多schema配置
 *
 * @author: humingchuan (humingchuan@youzan.com)
 * @date: 2022-06-24 23:16
 * @description
 */
@Data
public class BizFilterConfigs {
    private static BizFilterConfig DEFAULT = new BizFilterConfig();
    /**
     * 场景化配置
     */
    private Map<String, BizFilterConfig> schemas = new HashMap<>();

    @Nonnull
    public BizFilterConfig getBizFilterConfig(String scenario) {
        if (schemas == null) {
            return DEFAULT;
        }
        return schemas.getOrDefault(scenario, DEFAULT);
    }
}
