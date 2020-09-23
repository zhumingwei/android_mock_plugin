package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:23
 * @email zdf312192599@163.com
 */
public class MockMethodInjectDelegate extends AbstractAsmDelegate {

    public byte[] transformByte(byte[] bytes, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) {
        return super.transformByte(bytes,staticAsmConfig);
    }

    @Override
    public AsmHelper createHelper() {
        return new MockMethodInjectHelper();
    }
}
