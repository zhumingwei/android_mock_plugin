package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

import java.io.IOException;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:26
 * @email zdf312192599@163.com
 */
class MockMethodInjectHelper implements AsmHelper {
    @Override
    public byte[] modifyClass(byte[] srcClass, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) throws IOException {
        return ModifyUtils.modifyClass(srcClass,staticAsmConfig);
    }
}
