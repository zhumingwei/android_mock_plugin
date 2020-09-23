package com.bilibili.comicplugin.transform;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.bilibili.comicplugin.ComicPlugin;
import com.bilibili.comicplugin.base.BaseTransform;
import com.bilibili.comicplugin.inject.MockMethodInjectDelegate;
import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;
import com.bilibili.comicplugin.model.TransformCallBack;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zhumingwei
 * @date 2020/9/18 12:20
 * @email zdf312192599@163.com
 */
public class AsmTransform extends Transform {
    Project project;

    public AsmTransform(Project project) {
        this.project = project;
        log("create transform");
    }

    @Override
    public String getName() {
        return "AsmTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        long start = System.currentTimeMillis();
        log("do transform start Time==" + start);
        BaseTransform transform = new BaseTransform(transformInvocation, new TransformCallBack() {
            @Override
            public byte[] process(final String className, final byte[] bytes, BaseTransform transform) {

                final MockMethodInjectDelegate helper = new MockMethodInjectDelegate();
                if (!ClassChecker.checkClassName(className)) {
                    return null;
                }
                if (ComicPlugin.asmFunctionMockConfig.targetClassSet.contains(className)) {
                    return null;
                }
                byte[] resultbytes = bytes;
                List<Object> list = ComicPlugin.asmFunctionMockConfig.mStaticConfigList.stream().filter(new Predicate<AsmFunctionMockConfig.StaticAsmConfig>() {
                    @Override
                    public boolean test(AsmFunctionMockConfig.StaticAsmConfig staticAsmConfig) {
                        return staticAsmConfig.classFilter.call(className);
                    }
                }).collect(Collectors.toList());
                for (int i = 0; i < list.size(); i++) {
                    AsmFunctionMockConfig.StaticAsmConfig config = (AsmFunctionMockConfig.StaticAsmConfig) list.get(i);
                    resultbytes = helper.transformByte(resultbytes, config);
                }
                return resultbytes;
            }
        });
        transform.startTransform();
        long end = System.currentTimeMillis();

        log("do transform End Time==" + end);

        log("used time == " + (end - start));
    }

    public void log(String msg) {
        System.out.println(msg);
    }
}
