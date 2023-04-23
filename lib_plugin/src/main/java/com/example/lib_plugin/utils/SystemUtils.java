package com.example.lib_plugin.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

public class SystemUtils {

    @SuppressLint("PrivateApi")
    public static String getCPUType() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getDeclaredMethod("get", String.class);
            return (String) get.invoke(clazz, new Object[]{"ro.product.cpu.abi"});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
