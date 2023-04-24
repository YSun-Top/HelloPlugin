package com.example.app_view;

import android.os.Bundle;

import com.example.lib_plugin.PluginBaseActivity;
import com.example.lib_plugin.PluginType;

public class MainActivity extends PluginBaseActivity {

    @Override
    public PluginType getPluginType() {
        return PluginType.VIEW;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proxy.setContentView(R.layout.activity_main);
    }
}