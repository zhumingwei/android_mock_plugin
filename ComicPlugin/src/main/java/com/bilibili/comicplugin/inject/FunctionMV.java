package com.bilibili.comicplugin.inject;

import com.bilibili.comicplugin.model.AsmFunctionMockConfig;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * @author zhumingwei
 * @date 2020/9/22 15:26
 * @email zdf312192599@163.com
 */
public class FunctionMV extends MethodVisitor {
    private AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig;

    public FunctionMV(int api) {
        super(api);
    }

    public FunctionMV(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public FunctionMV(MethodVisitor mv, AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig){
        this(Opcodes.ASM5,mv);
        this.staticAsmConfig = staticAsmConfig;
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (staticAsmConfig.invokeMethodFilter.call(opcode, owner.replace("/", "."), name, desc, itf)){
            if (opcode==INVOKESTATIC) {
                mv.visitMethodInsn(INVOKESTATIC, staticAsmConfig.targetClass.replace(".", "/"), name, desc, itf);
            }else{
                String mdesc = desc.replace("(","(L"+owner+";");
                mv.visitMethodInsn(INVOKESTATIC, staticAsmConfig.targetClass.replace(".", "/"), name, mdesc, itf);
            }


        }else{
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}
