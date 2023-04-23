package com.example.lib_plugin.module;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

import com.example.lib_plugin.PluginManager;
import com.example.lib_plugin.PluginType;

import dalvik.system.DexClassLoader;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 20:03
 * @UpdateDate: 2023/4/23 20:03
 */
public class SettingPluginManager extends PluginManager {

    @Override
    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    @Override
    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    @Override
    public PluginType getPluginType() {
        return PluginType.SETTING;
    }

    @Override
    public String jniFolderName() {
        return "jniLibs_app_setting";
    }

    private static class PluginManagerHelper {
        static final SettingPluginManager plugin = new SettingPluginManager();
    }

    public static SettingPluginManager getInstance() {
        return PluginManagerHelper.plugin;
    }
}
