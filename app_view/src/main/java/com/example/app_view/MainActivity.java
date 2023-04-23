package com.example.app_view;

import android.os.Bundle;

import com.example.lib_plugin_child.PluginBaseActivity;

public class MainActivity extends PluginBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proxy.setContentView(R.layout.activity_main);
    }
}