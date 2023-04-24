package com.jandjdevlps.yedas;

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

    //Note Firebase Auth 객체 변수
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    //Note DATA Base 객체 변수
    DatabaseReference mDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        //Note 권한 점검
       if(isPermissionGranted())
       {
         initiageApp();
       }
       else
       {
           //Note 없을 경우 권한 요청.
           Log.v(TAG,"Permission is revoked");
           String[] requsetString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                   Manifest.permission.READ_EXTERNAL_STORAGE};
           ActivityCompat.requestPermissions(this,requsetString, 1);
       }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == 0)
        {
            boolean isPerpermissionForAllGranted = false;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (int i = 0; i < permissions.length; i++) {
                    isPerpermissionForAllGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            } else {
                isPerpermissionForAllGranted = true;
            }

            if (isPerpermissionForAllGranted) {
                initiageApp();
            } else {
                Toast.makeText(this, "앱이 실행되기 위한 필수조건을 충족하지 못하였으므로 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Note 앱 실행하기 위한 내역.
    public void initiageApp()
    {
        //Note 어차피 시작될거라면, Instance는 미리 생성.
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    //Note 작품 생성당시에는 안드로이드 8이하였기 때문에 일단 안드로이드10이상은 고려 대상이 아님.
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YEDAS/";
                    File root = new File(path);
                    //Note PDF를 가져와 저장하기 위한 경로.
                    if(!root.exists())
                    {
                        if(root.mkdirs())
                        {
                            Log.e(TAG,"Error on makindg Directory");
                        }
                    }

                    if(firebaseAuth.getCurrentUser()!=null&&firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        Toast.makeText(getApplicationContext(),"아이디 확인! 자동 로그인중입니다.",Toast.LENGTH_SHORT).show();
                        //Note 객체 생성
                        user = firebaseAuth.getCurrentUser();
                        mRef = mDatabase.child("User");
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User userd = dataSnapshot.child(user.getUid()).getValue(User.class);
                                try {
                                    String name = userd.getStrUsername();
                                    if(name!=null) {
                                        startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                                        Log.d(TAG, "LogInWithEmail:success");
                                        finish();
                                    }
                                }catch(NullPointerException e){
                                    Log.d(TAG, "error : "+e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "객체 생성 취소 : "+databaseError.getMessage());
                            }
                        });

                    }else{
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 3000);
    }

    public  boolean isPermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG,"Permission is granted");
                return true;
            }
            else
            {
                Log.v(TAG,"Permission need to be Granted");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
