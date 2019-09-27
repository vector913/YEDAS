package com.example.yedas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

public class LoadingScreenActivity extends AppCompatActivity {
    String TAG = "LoadingScreenActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try{
                    isPermissionGranted();
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YEDAS/";
                    File root = new File(path);
                    if(!root.exists()){
                        root.mkdirs();
                    }
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, 2000);
    }
    public  boolean isPermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
                    checkSelfPermission(Manifest.permission.INTERNET)== PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);

                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
