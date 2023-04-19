package com.example.helloplugin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //存放so文件的文件夹名，如/Assets/${soFolder}
    private final String soFolder = "libSo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void initAllPlugin() {
        File copyFile = getDir("jniLibs", Context.MODE_PRIVATE);
        if (!copyFile.exists()) {
            boolean result = copyFile.mkdir();
            if (!result) {
                Log.w("mainApp", "jniLibs 文件夹创建失败; path:" + copyFile.getPath());
            }
        }
        SoFile.CopyAssets(getApplicationContext(), soFolder, copyFile.getAbsolutePath());

    }
}
