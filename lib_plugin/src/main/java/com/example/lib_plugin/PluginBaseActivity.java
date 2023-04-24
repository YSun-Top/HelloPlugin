package com.example.lib_plugin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;

/**
 * @Description: 插件APP的Activity父类，所有的Activity都应该继承这个类
 * 它由 {@link com.example.lib_plugin.ProxyActivity} 这个类控制
 * @Author: Void
 * @CreateDate: 2023/4/23 20:46
 * @UpdateDate: 2023/4/23 20:46
 */
public abstract class PluginBaseActivity extends AppCompatActivity implements IPlugin, DefaultLifecycleObserver {
    protected AppCompatActivity proxy;
    private int from = FROM_INTERNAL;

    public abstract PluginType getPluginType();

    //因为插件是没有生命周期的所以必须从主apk中获取上下文，也是为了之后插件apk里的activity能够执行startActivity(){这个方法需要一个上下文}。
    //当第一个打插件的activity是采用直接调Activity的onCreate() {说白了就是直接new一个activity，而不是直接走Android正常的调一个Activity，所以没有生命周期}
    @Override
    public final void attach(AppCompatActivity proxyActivity) {
        proxy = proxyActivity;
        proxy.getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        if (from == FROM_INTERNAL) {
            super.onCreate(saveInstanceState);
            proxy = this;//如果是从内部跳转，那就将上下文定为自己{因为插件apk本身就是一个apk它也可以自己安装启动}
        } else {
            from = saveInstanceState.getInt("from");
        }
        //Activity内部当Title不为空时，会加载一个abc_screen_toolbar布局，这会导致空指针异常，设置为NO_TITLE
        proxy.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (from == FROM_INTERNAL) {
            return super.findViewById(id);
        } else {
            return proxy.findViewById(id);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (from == FROM_INTERNAL) {
            super.startActivity(intent);
            return;
        }
        PluginManager.getPluginManager(getPluginType()).gotoActivity(proxy,intent,getPluginType());
    }
}
