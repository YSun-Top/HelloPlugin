package com.example.lib_plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 17:05
 * @UpdateDate: 2023/4/23 17:05
 */
public class FileTools {
    /**
     * 计算文件MD5
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        try {
            return getFileMD5(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileMD5(InputStream inStream) {
        if (inStream == null) return null;
        MessageDigest digest;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = inStream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
