package com.yinfeng.wypzh.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

//    private File parent;


//    private static volatile FileUtils instance;
//
//    public static FileUtils getInstance() {
//        if (instance == null) {
//            synchronized (FileUtils.class) {
//                if (instance == null) {
//                    instance = new FileUtils();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private FileUtils() {
////        parent = initRootFileDir();
//    }

//    private File initRootFileDir() {
////        File appDir = new File(Environment.getExternalStorageDirectory(), "SafeAccompany");
//        File appDir = new File(Environment.getExternalStorageDirectory(), "无忧陪诊");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        if (!appDir.isDirectory())
//            appDir.mkdir();
//        return appDir;
//
//    }

    public static String initRootFilePath(Context context) {
        String storageRootPath = null;
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.getExternalCacheDir() != null) {
                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            File appDir = new File(Environment.getExternalStorageDirectory(), "无忧陪诊");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            if (!appDir.isDirectory())
                appDir.mkdir();
            storageRootPath = appDir.getAbsolutePath();
        }
        return storageRootPath;
    }


//    public String getFileRootPath() {
//        File file = initRootFileDir();
//        return file.getAbsolutePath();
//    }

    public static String getFilePath(Context context, String fileName) {
//        File file = new File(parent, fileName);
        String rootPath = initRootFilePath(context);
        File file = new File(rootPath, fileName);
        return file.getAbsolutePath();
    }

    public static File getFile(Context context, String fileName) {
        String rootPath = initRootFilePath(context);
        File file = new File(rootPath, fileName);
        return file;
    }
}
