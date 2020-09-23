package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:32
 * @email zdf312192599@163.com
 */
public class ModifyUtils {
    public static byte[] modifyClass(byte[] srcClass, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor methodFilterCV = new ClassFilterVisitor(classWriter,staticAsmConfig);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG);
        return classWriter.toByteArray();
    }
}
