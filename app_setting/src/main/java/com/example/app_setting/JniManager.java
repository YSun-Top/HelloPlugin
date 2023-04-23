package com.example.app_setting;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 18:37
 * @UpdateDate: 2023/4/23 18:37
 */
public class JniManager {

    public native String stringFromJNI();

    static {
        System.loadLibrary("hello_so");
    }
}
