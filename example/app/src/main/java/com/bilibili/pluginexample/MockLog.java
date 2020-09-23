package com.bilibili.pluginexample;

import android.util.Log;

import androidx.annotation.Keep;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:04
 * @email zdf312192599@163.com
 */
@Keep
public class MockLog {

    public static int d(String tag, String msg) {
        return Log.d(tag,"adsfas"+ msg + " ===== mock 所有Log代码");
    }
    public static int i(String tag, String msg) { return Log.i(tag,msg + " ===== mock 所有Log代码"); }
    public static int w(String tag, String msg) { return Log.w(tag,msg + " ===== mock 所有Log代码"); }
    public static int e(String tag, String msg) { return Log.e(tag,msg + " ===== mock 所有Log代码"); }
    public static String getHello(OriginMethod originMethod,String msg){
        return "hello +++ " + msg;
    }

}
