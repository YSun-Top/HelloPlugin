package com.example.helloplugin;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SoFile {

    /**
     * 将assets的资源复制出来，包括插件apk和对应的so文件
     * @param assetDir assets目录下存放文件的文件名
     * @param dir 要复制到哪个位置去
     */
    public static void CopyAssets(Context context, String assetDir, String dir) {
        String[] fileList;
        try {
            // 获得Assets的文件列表
            fileList = context.getResources().getAssets().list(assetDir);
        } catch (IOException e) {
            return;
        }
        File mWorkingPath = new File(dir);
        // 如果文件路径不存在
        if (!mWorkingPath.exists()) {
            // 创建文件夹
            mWorkingPath.mkdirs();// 文件夹创建不成功时调用
        }

        for (String fileName : fileList) {
            try {
                // 根据路径判断是文件夹还是文件
                if (!fileName.contains(".")) {
                    if (0 == assetDir.length()) {
                        CopyAssets(context, fileName, dir + fileName + "/");
                    } else {
                        CopyAssets(context, assetDir + "/" + fileName, dir + "/"
                                + fileName + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists())
                    outFile.delete();
                InputStream in;
                if (0 != assetDir.length())
                    in = context.getAssets().open(assetDir + "/" + fileName);
                else
                    in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);


                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
