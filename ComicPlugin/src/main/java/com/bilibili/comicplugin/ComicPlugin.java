package com.bilibili.comicplugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.bilibili.comicplugin.model.AsmConfig;
import com.bilibili.comicplugin.model.AsmFunctionMockConfig;
import com.bilibili.comicplugin.transform.AsmTransform;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Map;
import java.util.function.Consumer;

public class ComicPlugin implements Plugin<Project> {

    private static final String EXT_NAME = "asmConfig";
    public static AsmConfig asmConfig;
    public static AsmFunctionMockConfig asmFunctionMockConfig = null;
    @Override
    public void apply(Project project) {
        boolean isApp = project.getPlugins().hasPlugin(AppPlugin.class);
        project.getExtensions().create(EXT_NAME, AsmConfig.class);
        if (!isApp) {
            throw new GradleException(" this plugin is not application");
        }
        log("start plugin");

        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.registerTransform(new AsmTransform(project));
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                System.out.println("afterEvaluate transform");

                AsmConfig config = (AsmConfig) project.getExtensions().findByName(EXT_NAME);
                if (config == null) {
                    config = new AsmConfig();
                }
                init(config);
                System.out.println(config.toString());
                asmConfig = config;
            }
        });

    }

    public void init(AsmConfig config){
        asmFunctionMockConfig = new AsmFunctionMockConfig(config);
    }


    public static void log(String msg){
        System.out.println("comic_plugin_log:"+msg);
    }
}

