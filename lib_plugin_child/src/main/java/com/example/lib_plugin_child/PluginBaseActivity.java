package com.example.lib_plugin_child;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.lib_plugin.IPlugin;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 20:46
 * @UpdateDate: 2023/4/23 20:46
 */
public abstract class PluginBaseActivity extends Activity implements IPlugin {
    protected Activity proxy;//上下文
    private int from = FROM_EXTERNAL;


    //因为插件是没有生命周期的所以必须从主apk中获取上下文，也是为了之后插件apk里的activity能够执行startActivity(){这个方法需要一个上下文}。
    // 当第一个打插件的activity是采用直接调Activity的onCreate() {说白了就是直接new一个activity，而不是直接走Android正常的调一个Activity，所以没有生命周期}
    @Override
    public void attach(Activity proxyActivity) {
        //拿到外部的上下文
        proxy = proxyActivity;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        if (from == FROM_INTERNAL){
            super.onCreate(saveInstanceState);
            proxy = this;//如果是从内部跳转，那就将上下文定为自己{因为插件apk本身就是一个apk它也可以自己安装启动}
        }
    }

    @Override
    public void onStart() {
        if (from == FROM_INTERNAL) {
            super.onStart();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onStart()");
        }
    }

    @Override
    public void onResume() {
        if (from == FROM_INTERNAL) {
            super.onResume();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onResume()");
        }
    }

    @Override
    public void onRestart() {
        if (from == FROM_INTERNAL) {
            super.onRestart();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onRestart()");
        }
    }

    @Override
    public void onPause() {
        if (from == FROM_INTERNAL) {
            super.onPause();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onPause()");
        }
    }

    @Override
    public void onStop() {
        if (from == FROM_INTERNAL) {
            super.onStop();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onStop()");
        }
    }

    @Override
    public void onDestroy() {
        if (from == FROM_INTERNAL) {
            super.onDestroy();
        } else {
            Log.d("PluginBaseActivity", "宿主启动：onDestroy()");
        }
    }
}
