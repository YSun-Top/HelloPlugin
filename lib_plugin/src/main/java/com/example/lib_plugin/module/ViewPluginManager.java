package com.example.lib_plugin.module;

import android.content.pm.PackageInfo;
import android.content.res.Resources;

import com.example.lib_plugin.PluginManager;
import com.example.lib_plugin.PluginType;

import dalvik.system.DexClassLoader;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 20:02
 * @UpdateDate: 2023/4/23 20:02
 */
public class ViewPluginManager extends PluginManager {

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
        return PluginType.VIEW;
    }

    @Override
    public String getApkName() {
        return "app_view-release.apk";
    }

    @Override
    public String jniFolderName() {
        return null;
    }

    private static class PluginManagerHelper {
        static final ViewPluginManager plugin = new ViewPluginManager();
    }

    public static ViewPluginManager getInstance() {
        return PluginManagerHelper.plugin;
    }
}
