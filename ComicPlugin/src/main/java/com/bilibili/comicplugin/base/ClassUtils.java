package com.bilibili.comicplugin.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zhumingwei
 * @date 2020/9/21 13:54
 * @email zdf312192599@163.com
 */
public class ClassUtils {
    public static String path2Classname(String entryName) {
        return entryName.replace(File.separator, ".").replace(".class", "");
    }

    public static String className2Path(String className){
        return className.replace(".",File.separator);
    }

    public static File saveFile(File tmpFile, byte[] modifiedBytes) throws IOException {
        File modified = null;
        modified = tmpFile;
        if (modified.exists()) {
            modified.delete();
        }
        modified.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(modified);
        fileOutputStream.write(modifiedBytes);
        fileOutputStream.close();
        return modified;
    }
}
