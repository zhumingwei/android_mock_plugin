package com.bilibili.comicplugin.transform;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:07
 * @email zdf312192599@163.com
 */
public class ClassChecker {
    public static boolean checkRName(String className) {
        return className.contains("R$") || className.endsWith("R");
    }

    public static boolean checkBuildConfig(String className){
        return className.endsWith("BuildConfig");
    }

    public static boolean checkClassName(String className) {
        return !checkRName(className) && !checkBuildConfig(className);
    }
}
