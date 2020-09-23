package com.bilibili.comicplugin.base;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.bilibili.comicplugin.model.TransformCallBack;
import com.google.common.io.Files;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author zhumingwei
 * @date 2020/9/21 10:21
 * @email zdf312192599@163.com
 */
public class BaseTransform {

    private final TransformInvocation transformInvocation;
    private final TransformCallBack callBack;
    private final Context context;
    private final Collection<TransformInput> inputs;
    private final TransformOutputProvider outputProvider;
    private final boolean isIncremental;
    private DeleteCallBack deleteCallBack;
    private boolean simpleScan = false;

    public BaseTransform(TransformInvocation transformInvocation, TransformCallBack callBack) {
        this.transformInvocation = transformInvocation;
        this.callBack = callBack;
        this.context = transformInvocation.getContext();
        this.inputs = transformInvocation.getInputs();
        this.outputProvider = transformInvocation.getOutputProvider();
        isIncremental = transformInvocation.isIncremental();
    }

    public void setDeleteCallBack(DeleteCallBack deleteCallBack) {
        this.deleteCallBack = deleteCallBack;
    }

    public void startTransform() {
        try {
            if (!isIncremental) {
                outputProvider.deleteAll();
            }
            for (TransformInput input : inputs) {
                for (JarInput jarInput : input.getJarInputs()) {
                    Status status = jarInput.getStatus();
                    String destName = jarInput.getFile().getName();
                    /* 重命名输出文件,因为可能同名,会覆盖*/
                    String hexName = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath()).substring(0, 8);
                    if (destName.endsWith(".jar")) {
                        destName = destName.substring(0, destName.length() - 4);
                    }
                    /*获得输出文件*/
                    File dest = outputProvider.getContentLocation(destName + "_" + hexName,
                            jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                    if (isIncremental) {
                        switch (status) {
                            case NOTCHANGED:
                                break;
                            case ADDED:
                                foreachJar(dest, jarInput);
                                break;
                            case CHANGED:
                                diffJar(dest, jarInput);
                                break;
                            case REMOVED:
                                deleteScan(dest);
                                if (dest.exists()) {
                                    FileUtils.forceDelete(dest);
                                }
                                break;

                        }
                    } else {
                        foreachJar(dest, jarInput);
                    }
                }
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    foreachClass(directoryInput);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void foreachJar(File dest, JarInput jarInput) throws IOException {
        if (simpleScan) {
            File jarFile = jarInput.getFile();
            HashSet<String> classNames = JarUtils.scanJarFile(jarFile);
            for (String className : classNames) {
                callBack.process(ClassUtils.path2Classname(className), null, this);
            }
            FileUtils.copyFile(jarFile, dest);
        } else {
            File modifiedJar = JarUtils.modifyJarFile(jarInput.getFile(), context.getTemporaryDir(),
                    callBack, this);
            if (modifiedJar == null) {
                modifiedJar = jarInput.getFile();
            }
            FileUtils.copyFile(modifiedJar, dest);
        }

    }

    private void foreachClass(DirectoryInput directoryInput) throws IOException {
        File dest = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(),
                directoryInput.getScopes(), Format.DIRECTORY);
        File dir = directoryInput.getFile();
        Map<File, Status> map = directoryInput.getChangedFiles();
        if (isIncremental) {
            for (Map.Entry<File, Status> entry : map.entrySet()) {
                Log.info("entry:" + entry.getKey().getAbsolutePath());
                Status status = entry.getValue();
                File file = entry.getKey();
                String destFilePath = file.getAbsolutePath().replace(dir.getAbsolutePath(), dest.getAbsolutePath());
                File destFile = new File(destFilePath);
                Log.info("destFilePath:" + destFilePath);
                switch (status) {
                    case NOTCHANGED:
                        break;
                    case ADDED:
                    case CHANGED:
                        Files.createParentDirs(destFile);
                        FileUtils.touch(destFile);
                        modifySingleFile(dir, file, destFile);
                        break;
                    case REMOVED:
                        Log.info(entry);
                        deleteDirectory(destFile, dest);
                        break;
                }
            }
        } else {
            changeFile(dir, dest);
        }
    }

    private void modifySingleFile(File dir, File file, File dest) throws IOException {
        String absolutePath = file.getAbsolutePath().replace(dir.getAbsolutePath() + File.separator, "");
        String className = ClassUtils.path2Classname(absolutePath);
        if ((absolutePath.endsWith(".class"))) {
            byte[] modifiedBytes = null;
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            fileInputStream.close();
            modifiedBytes = callBack.process(className, bytes, this);
            if (modifiedBytes == null) {
                modifiedBytes = bytes;
            }
            ClassUtils.saveFile(dest, modifiedBytes);
        } else {
            FileUtils.copyFile(file, dest);
        }

    }

    private void deleteDirectory(File destFile, File dest) throws IOException {
        if (destFile.isDirectory()) {
            for (File classFile : com.android.utils.FileUtils.getAllFiles(destFile)) {
                deleteSingle(classFile, dest);
            }
        } else {
            deleteSingle(destFile, dest);
        }
        if (destFile.exists()) {
            FileUtils.forceDelete(destFile);
        }
    }

    private void deleteSingle(File classFile, File dest) throws IOException {
        if (classFile.getName().endsWith(".class")) {
            String absolutePath = classFile.getAbsolutePath().replace(dest.getAbsolutePath() +
                    File.separator, "");
            String className = ClassUtils.path2Classname(absolutePath);
            byte[] bytes = IOUtils.toByteArray(new FileInputStream(classFile));
            if (deleteCallBack != null) {
                deleteCallBack.delete(className, bytes);
            }
        }
    }

    private void changeFile(File dir, File dest) throws IOException {
        if (dir.isDirectory()) {
            HashMap<String, File> modifyMap = new HashMap<>();
            for (File classFile : com.android.utils.FileUtils.getAllFiles(dir)) {
                if (classFile.getName().endsWith(".class")) {
                    String absolutePath = classFile.getAbsolutePath().replace(dir.getAbsolutePath() + File.separator, "");
                    String className = ClassUtils.path2Classname(absolutePath);
                    if (simpleScan) {
                        callBack.process(className, null, this);
                    } else {
                        byte[] bytes = IOUtils.toByteArray(new FileInputStream(classFile));
                        byte[] modifiedBytes = callBack.process(className, bytes, this);
                        if (modifiedBytes == null) {
                            modifiedBytes = bytes;
                        }
                        File modified = ClassUtils.saveFile(classFile, modifiedBytes);
                        if (modified != null) {
                            //key为相对路径
                            modifyMap.put(classFile.getAbsolutePath().replace(dir.getAbsolutePath(), ""), modified);
                        }
                    }
                }
            }
            FileUtils.copyDirectory(dir, dest);
//            for (Map.Entry<String, File> en : modifyMap.entrySet()) {
//                File target = new File(dest.getAbsolutePath() + en.getKey());
//                if (target.exists()) {
//                    target.delete();
//                }
//                FileUtils.copyFile(en.getValue(), target);
//                en.getValue().delete();
//            }

        }
    }

    private void diffJar(File dest, JarInput jarInput) {
        try {
            HashSet<String> oldJarFileName = JarUtils.scanJarFile(dest);
            HashSet<String> newJarFileName = JarUtils.scanJarFile(jarInput.getFile());
            SetDiff<String> diff = new SetDiff<>(oldJarFileName, newJarFileName);
            List<String> removeList = diff.getRemovedList();
            if (removeList.size() > 0) {
                JarUtils.deleteJarScan(dest, removeList, deleteCallBack);
            }
            foreachJar(dest, jarInput);
        } catch (Exception ex) {

        }
    }

    private void deleteScan(File dest) {
        try {
            JarUtils.deleteJarScan(dest, deleteCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
