package com.example.lib_plugin;

import android.app.Activity;
import android.os.Bundle;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 18:52
 * @UpdateDate: 2023/4/23 18:52
 */
public interface IPlugin {
    int FROM_INTERNAL = 0;//插件单独测试时的内部跳转 (插件apk自己跟自己)
    int FROM_EXTERNAL = 1;//宿主执行的跳转逻辑 （从主APK进入插件apk）

    /**
     * 给插件Activity指定上下文
     */
    void attach(Activity activity);

    // 以下全都是Activity生命周期函数,
    // 插件Activity本身 在被用作"插件"的时候不具备生命周期，由宿主里面的代理Activity类代为管理
    void onCreate(Bundle saveInstanceState);

    void onStart();

    void onResume();

    void onRestart();

    void onPause();

    void onStop();

    void onDestroy();
}
