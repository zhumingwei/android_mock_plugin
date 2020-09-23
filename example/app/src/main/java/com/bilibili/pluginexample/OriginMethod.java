package com.bilibili.pluginexample;

import android.util.Log;

/**
 * @author zhumingwei
 * @date 2020/9/23 13:16
 * @email zdf312192599@163.com
 */
class OriginMethod {
    public static void log(){
        Log.d("log","log");
    }

    public void onCreate(){

        String hello = "hello";
        Log.e("adsfasfeee",getHello(hello));
    }

    public String getHello(String hello){
        return "hello";
    }

}
