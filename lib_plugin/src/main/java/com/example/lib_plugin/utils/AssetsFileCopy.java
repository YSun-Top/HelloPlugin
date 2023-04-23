package com.example.lib_plugin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lib_plugin.PluginCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 用于从Assets文件夹中复制资源文件
 * @Author: Void
 * @CreateDate: 2023/4/23 15:49
 * @UpdateDate: 2023/4/23 15:49
 */
public class AssetsFileCopy {
    private ExecutorService execute = Executors.newSingleThreadExecutor();
    private SharedPreferences sp;

    public void searchFile(Context context) {
        searchFile(context, "");
    }

    /**
     * 遍历assets中的文件，根据不同文件后缀放入不同文件夹中
     *
     * @param assetDir assets目录下的文件夹名，传“”表示根目录
     */
    public void searchFile(Context context, String assetDir) {
        if (assetDir == null) return;
        String[] fileList;
        try {
            fileList = context.getResources().getAssets().list(assetDir);
        } catch (IOException e) {
            //当获取的文件列表不存在
            return;
        }
        for (String fileStr : fileList) {
            if (!fileStr.endsWith(".so") && !fileStr.endsWith(".apk")) {
                if (assetDir.isEmpty()) {
                    searchFile(context, fileStr);
                } else {
                    searchFile(context, assetDir + "/" + fileStr);
                }
                continue;
            }
            //找到文件后开始运行文件复制流程
            File targetPath;
            if (fileStr.endsWith(".so")) {
                targetPath = context.getDir("jniLibs", Context.MODE_PRIVATE);
            } else {
                targetPath = context.getDir("plugin", Context.MODE_PRIVATE);
            }
            copyFile(context, targetPath, assetDir, fileStr);
        }
    }

    /**
     * 复制文件
     *
     * @param targetPath 复制文件到此处
     * @param filePath   待复制文件的路径
     * @param fileName   文件名
     */
    public void copyFile(Context context, File targetPath, String filePath, String fileName) {
        File targetFolder = new File(targetPath, filePath);
        targetFolder.mkdirs();
        File outFile = new File(targetFolder, fileName);
        if (!isUpdateAndCopyFile(context, outFile)) {
            Log.d(PluginCode.TAG, "md5相等,文件不用更新");
            return;
        }
        execute.execute(() -> {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                if (0 != filePath.length())
                    in = context.getAssets().open(filePath + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                out = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException ignored) {
            } finally {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (IOException ignored) {
                }
            }
        });
    }

    /**
     * 检查文件是否需要复制并更新
     *
     * @return true需要复制更新
     */
    private boolean isUpdateAndCopyFile(Context context, File file) {
        //文件不存在，需要更新文件
        if (!file.exists()) return true;
        sp = context.getSharedPreferences(AssetsFileCopy.class.getName(), Context.MODE_PRIVATE);
        //文件存在但在SharedPreferences中没有记录(比如通过其他地方写入的文件)：true
        if (!sp.contains(file.getPath()))
            return true;
        String md5 = FileTools.getFileMD5(file);
        String oldFileMD5 = sp.getString(file.getPath(), "");
        //md5相等时不用更新文件
        if (oldFileMD5.equals(md5)) {
            return false;
        }
        sp.edit().putString(file.getPath(), md5).apply();
        return true;
    }

    private static final class AssetsFileCopyHolder {
        static final AssetsFileCopy soFile = new AssetsFileCopy();
    }

    public static AssetsFileCopy getInstance() {
        return AssetsFileCopyHolder.soFile;
    }

    private AssetsFileCopy() {
    }
}
