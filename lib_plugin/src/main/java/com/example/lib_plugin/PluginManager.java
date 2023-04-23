package com.example.lib_plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lib_plugin.module.SettingPluginManager;
import com.example.lib_plugin.module.ViewPluginManager;
import com.example.lib_plugin.utils.AssetsFileCopy;
import com.example.lib_plugin.utils.SystemUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @Description: java类作用描述
 * @Author: Void
 * @CreateDate: 2023/4/23 16:36
 * @UpdateDate: 2023/4/23 16:36
 */
public abstract class PluginManager {
    public static final String TAG_CLASS_NAME = "className";
    public static final String TAG_FROM = "from";
    public static final String TYPE = "PluginType"; //识别那个插件管理

    private WeakReference<Context> weakReference;
    protected PackageInfo packageInfo;//包信息
    protected DexClassLoader dexClassLoader;//类加载器
    protected Resources resources;//资源包

    public abstract PackageInfo getPackageInfo();

    public abstract DexClassLoader getDexClassLoader();

    public abstract Resources getResources();

    public abstract PluginType getPluginType();

    /**
     * assets目录下存放so文件的文件夹名字
     */
    public abstract String jniFolderName();

    /**
     * 根据类型获取插件apk路径
     *
     * @return apk的路径
     */
    public String getPluginApkByType() {
        File externalFilesDir = weakReference.get().getDir("plugin", Context.MODE_PRIVATE);
        StringBuilder sBuilder = new StringBuilder(externalFilesDir.getPath());
        sBuilder.append(File.separator);
        switch (getPluginType()) {
            case VIEW:
                sBuilder.append("app_view-release.apk");
                break;
            case SETTING:
                sBuilder.append("app_setting-release.apk");
                break;
            default:
                return null;
        }
        return sBuilder.toString();
    }

    public void loadPlugin(Context context, LoadCallback callback) {
        weakReference = new WeakReference<>(context);
        loadPlugin(callback);
    }

    private void loadPlugin(@NonNull LoadCallback callback) {
        AssetsFileCopy.getInstance().searchFile(weakReference.get());
        String apkPath = getPluginApkByType();
        Log.d(PluginCode.TAG, "loadPlugin-apkPath:" + apkPath);
        File f = new File(apkPath);
        if (!f.exists()) {
            callback.onResult(getPluginType(), false);
            return;
        }

        packageInfo = weakReference.get().getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null) {
            callback.onResult(getPluginType(), false);
            return;
        }
        Log.e(PluginCode.TAG, "包信息获取成功");

        //类加载器，DexClassLoader专门负责外部dex的类
        File outFile = weakReference.get().getDir("odex", Context.MODE_PRIVATE);
        File pluginFolder = weakReference.get().getDir("jniLibs", Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        sb.append(pluginFolder.getPath()).append(File.separator);
        if (jniFolderName() != null && jniFolderName().length() != 0) {
            sb.append(jniFolderName()).append(File.separator).append(SystemUtils.getCPUType());
        }
        dexClassLoader = new DexClassLoader(apkPath, outFile.getAbsolutePath(), sb.toString(), weakReference.get().getClassLoader());

        //创建AssetManager，然后创建Resources
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            method.invoke(assetManager, apkPath);
            resources = new Resources(assetManager, weakReference.get().getResources().getDisplayMetrics(), weakReference.get().getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        callback.onResult(getPluginType(), true);
    }

    public void gotoActivity(Context context, @NonNull String realActivityClassName, PluginType type) {
        ProxyActivity.newStartActivity(context, realActivityClassName, type);
    }

    public interface LoadCallback {
        void onResult(PluginType type, boolean isSuccess);
    }

    public static PluginManager getPluginManager(PluginType type) {
        switch (type) {
            case VIEW:
                return ViewPluginManager.getInstance();
            case SETTING:
                return SettingPluginManager.getInstance();
            default:
                throw new RuntimeException();
        }
    }
}
