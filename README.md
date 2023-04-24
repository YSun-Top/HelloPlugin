# HelloPlugin
这是一个插件化设计模式的Demo。
实现了宿主App启动插件App的功能。

### 什么是插件化？
一个项目中有多个Module，它们可以分为一个宿主App和多个插件App，它们之间没有依赖关系。根据需求宿主App可以动态加载插件App中的内容，启动apk中的Activity。
在开发过程中插件App是一个独立的项目，可以直接运行，而不用通过宿主App来预览新增的功能。

## 使用方式
不论是宿主App还是插件App都需要依赖`lib_plugin`这个Library。

### 插件App
这里以Jni项目举例。
在开始前应该注意几点：
1. 插件APP项目中所有的Activity都应该继承`PluginBaseActivity`
2. 项目中用到Activity的方法需要使用宿主App传过来的context
如：
```java
proxy.setContentView(R.layout.activity_main);
TextView tv = proxy.findViewById(R.id.tv);
```
这是因为插件App是通过`DexClassLoader`加载的，所以它没有Context。

回到插件App的使用方式，可以分几步：
1. 项目的Activity需要继承`PluginBaseActivity`
2. 用到的so文件需要复制到`lib_plugin`的Assets目录下
3. 打包apk，然后把apk复制到`lib_plugin`Assets目录

### 宿主APP
在`PluginType`枚举类添加一个插件类型，然后继承Lib中的`PluginManager`类并实现其抽象的方法：
```java
public class SettingPluginManager extends PluginManager {
    //...
    @Override
    public PluginType getPluginType() {
        return PluginType.SETTING;
    }

    @Override
    public String getApkName() {
        return "app_setting-release.apk";
    }

    @Override
    public String jniFolderName() {
        return "jniLibs_app_setting";
    }
    //...
}
```
注：每个插件APP都需要在宿主APP中实现一个`PluginManager`实例

接着在需要的时候对插件执行初始化：
```java
SettingPluginManager.getInstance().loadPlugin(context, callback)
```
需要传2个参数，第二个参数是加载结果回调，Lib内部处理了apk的复制、获取报信息、so文件复制、结果回调等。

如果加载结果返回的是成功，就可以执行跳转到插件App操作了：
```java
ViewPluginManager.getInstance().gotoActivity(getApplicationContext(), "com.example.app_view.MainActivity", type);
```
这里有3个参数，第二个参数是完整目标Activity完整包名，第三个参数是开头定义的`PluginType`，应该和准备打开的插件类型一致。

## 项目结构
项目核心实现都是在`lib_plugin`处理。为了方便您更快看懂这个项目，这里会详细描述这个类的结构。
1. PluginManager
   1. 插件管理类，负责插件的加载、插件页面的打开等。
   2. 内部定义了一些抽象的方法，PluginManager会通过这些类获取到存放在Assets目录下的app文件名、插件类型等
2. AssetsFileCopy： 负责从Assets中复制插件app和so文件。(当然你也可以实现一套下载功能，从服务器下载文件然后再加载)
3. PluginBaseActivity
   1. 插件APP的Activity父类，所有的Activity都应该继承这个类
   2. 它由`ProxyActivity`启动和控制
   3. 主要是管理插件Activity的生命周期和Context
4. ProxyActivity
   1. 代理Activity，上面的`PluginManager#gotoActivity()`就是通过启动这个类，来间接启动插件App的Activity。
   2. 内部通过`DexClassLoader`new了一个Activity并管理其生命周期和Context
   

## 项目实现思路
我们知道apk是一个压缩包，而apk动态加载就是将apk解压然后取出其中的资源通过特定的方法加载并显示出来。

1. 我们要先拿到apk文件，比如下载、Assets拷贝
2. 得到apk文件后通过`getPackageManager().getPackageArchiveInfo()`方法`PackageInfo`类。它描述了apk的包信息，对应了`AndroidManifest.xml`。比如apk编译版本，最低支持的版本号，Activity列表等
3. 然后通过apk文件拿到`DexClassLoader`类，它用于解析apk文件内的dex文件，dex文件就是我们的代码文件。
4. 然后要通过反射的方式将apk文件中的资源添加到`AssetManager`，这样在插件App中才能拿到App中的图片资源
