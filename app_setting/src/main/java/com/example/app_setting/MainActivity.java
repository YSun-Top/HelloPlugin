package com.example.app_setting;

import android.os.Bundle;
import android.widget.TextView;

import com.example.lib_plugin.PluginBaseActivity;
import com.example.lib_plugin.PluginType;

public class MainActivity extends PluginBaseActivity {

    @Override
    public PluginType getPluginType() {
        return PluginType.SETTING;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        proxy.setContentView(R.layout.activity_main);
        TextView tv = proxy.findViewById(R.id.tv);
        String str=new JniManager().stringFromJNI();
        tv.setText(str);
    }
}