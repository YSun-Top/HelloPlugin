package com.example.app_setting;

import android.os.Bundle;
import android.widget.TextView;

import com.example.lib_plugin_child.PluginBaseActivity;

public class MainActivity extends PluginBaseActivity {
    private TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proxy.setContentView(R.layout.activity_main);
        tv=proxy.findViewById(R.id.tv);
        String str=new JniManager().stringFromJNI();
        tv.setText(str);
    }
}