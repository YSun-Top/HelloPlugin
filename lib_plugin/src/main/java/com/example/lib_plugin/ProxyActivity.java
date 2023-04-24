package com.example.lib_plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_plugin.module.SettingPluginManager;
import com.example.lib_plugin.module.ViewPluginManager;

/**
 * @Description: 代理Activity。接收宿主App的跳转意图，然后将插件App的界面拉起
 * @Author: Void
 * @CreateDate: 2023/4/23 18:29
 * @UpdateDate: 2023/4/23 18:29
 */
public class ProxyActivity extends AppCompatActivity {
    private IPlugin iPlugin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean flag = getIntent().getBooleanExtra(FLAG, false);
        if (!flag) {
            throw new RuntimeException("请使用newStartActivity()启动代理Activity");
        }
        //宿主，将真正的跳转意图，放在了这个参数className中，
        String realActivityName = getIntent().getStringExtra(PluginManager.TAG_CLASS_NAME);
        Log.d(PluginCode.TAG, "ProxyActivity:" + realActivityName);
        //拿到realActivityName，接下来的工作，自然就是展示出真正的Activity
        try {
            // 通过DexClassLoader拿到目标Activity
            Class<?> realActivityClz = PluginManager.getPluginManager(type).getDexClassLoader().loadClass(realActivityName);
            Object obj = realActivityClz.newInstance();
            if (!(obj instanceof  IPlugin)){
                throw new RuntimeException("插件APK的Activity应该是IPlugin的子类！");
            }
            iPlugin = (IPlugin) obj;
            iPlugin.attach(this);
            //反射创建的插件Activity的生命周期函数不会被执行，那么，就由ProxyActivity代为执行
            Bundle bundle=new Bundle();
            bundle.putInt(PluginManager.TAG_FROM,IPlugin.FROM_EXTERNAL);
            iPlugin.onCreate(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (iPlugin == null) finish();
    }

    @Override
    public ClassLoader getClassLoader() {
        switch (type) {
            case VIEW:
                return ViewPluginManager.getInstance().getDexClassLoader();
            case SETTING:
                return SettingPluginManager.getInstance().getDexClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    public Resources getResources() {
        switch (type) {
            case VIEW:
                return ViewPluginManager.getInstance().getResources();
            case SETTING:
                return SettingPluginManager.getInstance().getResources();
        }
        return super.getResources();
    }

    //用于检查是否是从newStartActivity()启动了当前Activity。禁止从其他地方启动
    private static final String FLAG = "FLAG";
    private static PluginType type; //判断是那个插件Manager

    public static void newStartActivity(Context context, @NonNull String realActivityClassName, PluginType type) {
        if (realActivityClassName.isEmpty()) {
            throw new NullPointerException("class Name connote be null");
        }
        ProxyActivity.type = type;
        Intent intent = new Intent(context, ProxyActivity.class);
        intent.putExtra(FLAG, true);
        intent.putExtra(PluginManager.TAG_CLASS_NAME, realActivityClassName);
        intent.putExtra(PluginManager.TAG_FROM, IPlugin.FROM_EXTERNAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void newStartActivity(Context context,@NonNull Intent intent,PluginType type){
        ProxyActivity.type = type;
        intent.setComponent(new ComponentName(context, ProxyActivity.class));
        intent.putExtra(FLAG, true);
        intent.putExtra(PluginManager.TAG_CLASS_NAME, intent.getComponent().getClassName());
        intent.putExtra(PluginManager.TAG_FROM, IPlugin.FROM_EXTERNAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
