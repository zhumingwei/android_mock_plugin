package com.bilibili.comicplugin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import groovy.lang.Closure;

/**
 * @author zhumingwei
 * @date 2020/9/23 17:19
 * @email zdf312192599@163.com
 */
public class AsmFunctionMockConfig {
    public List<StaticAsmConfig> mStaticConfigList = new ArrayList<>();
    public Set targetClassSet = new HashSet();

    public AsmFunctionMockConfig(AsmConfig config) {
        if(config.staticConfigList!=null){
            config.staticConfigList.forEach(new Consumer<Map<String, Object>>() {
                @Override
                public void accept(Map<String, Object> stringObjectMap) {
                    mStaticConfigList.add(new AsmFunctionMockConfig.StaticAsmConfig(stringObjectMap));
                    targetClassSet.add((String) stringObjectMap.get("targetClass"));

                }
            });
        }
    }

    public static class StaticAsmConfig{
        public StaticAsmConfig(Map<String, Object> map) {
            methodFilter = (Closure<Boolean>) map.get("methodFilter");
            classFilter = (Closure<Boolean>) map.get("classFilter");
            targetClass = (String) map.get("targetClass");
            invokeMethodFilter = (Closure<Boolean>) map.get("invokeMethodFilter");
        }

        public Closure<Boolean> methodFilter;

        public Closure<Boolean> classFilter;

        public String targetClass;

        public Closure<Boolean> invokeMethodFilter;
    }
}
