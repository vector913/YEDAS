package com.example.yedas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class LoadingScreenActivity extends AppCompatActivity {
    String TAG = "LoadingScreenActivity";

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    DatabaseReference mRef;
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
                    firebaseAuth = FirebaseAuth.getInstance();
                    if(firebaseAuth.getCurrentUser()!=null&&firebaseAuth.getCurrentUser().isEmailVerified()){
                        Toast.makeText(getApplicationContext(),"아이디 확인! 자동 로그인중입니다.",Toast.LENGTH_SHORT).show();
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mRef = mDatabase.child("User");
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User userd = dataSnapshot.child(user.getUid()).getValue(User.class);
                                try {
                                    String name = userd.getUsername();
                                    if(name!=null) {
                                        startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                                        Log.d(TAG, "LogInWithEmail:success");
                                        finish();
                                    }
                                }catch(NullPointerException e){
                                    Log.d(TAG, "error : "+e);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }else{
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, 1500);
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
