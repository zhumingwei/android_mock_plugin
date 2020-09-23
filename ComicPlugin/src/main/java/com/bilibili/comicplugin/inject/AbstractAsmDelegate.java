package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:24
 * @email zdf312192599@163.com
 */
abstract class AbstractAsmDelegate {
    private AsmHelper asmHelper = createHelper();


    public byte[] transformByte(byte[] sourceClassBytes, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) {
        try {
            byte[] modifiedClassBytes = asmHelper.modifyClass(sourceClassBytes,staticAsmConfig);
            return modifiedClassBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract AsmHelper createHelper();
}
