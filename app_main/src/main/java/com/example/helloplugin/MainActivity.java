package com.example.helloplugin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lib_plugin.PluginCode;
import com.example.lib_plugin.PluginManager;
import com.example.lib_plugin.PluginType;
import com.example.lib_plugin.module.SettingPluginManager;
import com.example.lib_plugin.module.ViewPluginManager;

/**
 * 宿主APP，用于加载插件App
 */
public class MainActivity extends AppCompatActivity implements PluginManager.LoadCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        btn1.setText("view");
        btn2.setText("setting");
        btn1.setOnClickListener(v -> {
            ViewPluginManager.getInstance().loadPlugin(getApplicationContext(), this);
        });
        btn2.setOnClickListener(v -> {
            SettingPluginManager.getInstance().loadPlugin(getApplicationContext(), this);
        });
    }


    @Override
    public void onResult(PluginType type, boolean isSuccess) {
        if (!isSuccess) {
            Log.w(PluginCode.TAG, "加载apk失败");
            return;
        }
        Log.w(PluginCode.TAG, "加载apk成功");
        switch (type) {
            case VIEW:
                ViewPluginManager.getInstance().gotoActivity(getApplicationContext(), "com.example.app_view.MainActivity", type);
                break;
            case SETTING:
                SettingPluginManager.getInstance().gotoActivity(getApplicationContext(), "com.example.app_setting.MainActivity", type);
                break;
        }
    }
}
