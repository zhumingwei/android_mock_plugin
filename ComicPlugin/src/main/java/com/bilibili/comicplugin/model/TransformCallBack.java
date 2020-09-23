package com.bilibili.comicplugin.model;

import com.bilibili.comicplugin.base.BaseTransform;

public interface TransformCallBack {

    byte[] process(String className, byte[] classBytes, BaseTransform transform);

}
