package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.ComicPlugin;
import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.function.Predicate;

/**
 * @author zhumingwei
 * @date 2020/9/21 15:33
 * @email zdf312192599@163.com
 */
public class ClassFilterVisitor extends ClassVisitor {
    private AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig = null;

    ClassFilterVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    public ClassFilterVisitor(int api) {
        super(api);
    }

    public ClassFilterVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public ClassFilterVisitor(ClassWriter classWriter, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) {
        this(classWriter);
        this.staticAsmConfig = staticAsmConfig;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {


        if (staticAsmConfig.methodFilter.call(access, name.replace("/", "."), desc, signature, exceptions)) {
            MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
            return new CompleteMV(methodVisitor,staticAsmConfig);
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }
}