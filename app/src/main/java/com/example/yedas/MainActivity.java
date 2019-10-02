package com.example.yedas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


public class MainActivity extends AppCompatActivity{
    TextView user_id,user_name,user_job,user_dept;
    Button btnDeleteUser,btnLogout, btngoback, btn_set_usr_info;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener  authStateListener;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference fDatabase;
    private DatabaseReference fRef;
    String names, emaisl, depts,jobs;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageRef = storage.getReferenceFromUrl("gs://yedas-e5423.appspot.com");
    StorageReference pathReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting);

        user_id =  findViewById(R.id.setting_user_id);
        user_name = findViewById(R.id.setting_user_name);
        user_job = findViewById(R.id.setting_user_job);
        user_dept = findViewById(R.id.setting_department);


        btnDeleteUser = findViewById(R.id.deleteuser);
        btnLogout = findViewById(R.id.confirm);
        btngoback = findViewById(R.id.go_backs);
        btn_set_usr_info = findViewById(R.id.setting_set);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef    = mDatabase.child("User");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        final FirebaseUser user  = firebaseAuth.getCurrentUser();
        if(user!=null) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User userd = dataSnapshot.child(user.getUid()).getValue(User.class);
                        if(userd!=null) {
                            emaisl = userd.getEmail();
                            depts = userd.getDepartment();
                            jobs = userd.getJob();
                            names = userd.getUsername();
                            user_id.setText(emaisl);
                            user_name.setText(names);
                            user_dept.setText(depts);
                            user_job.setText(jobs);


                        }else{
                            user_id.setText(user.getEmail());
                            user_name.setText("정보를 수정하셔야 합니다.");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("MainActivity","Error Occurred");
                    }
                });
        }else{
          user_id.setText("no user know");
          btn_set_usr_info.setClickable(false);
        }



        btn_set_usr_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("정보수정")
                        .setMessage("사용자 정보를 수정하러 가시겠습니까?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //if(user.isEmailVerified()&&(user!=null)) {
                                    startActivity(new Intent(getApplicationContext(), UserInfoChangeActivity.class));
                                    finish();
                              //  }else{
                                //    Toast.makeText(getApplicationContext(),"이미 인증을 받은 유저입니다.",Toast.LENGTH_SHORT).show();
                             //   }
                            }
                        }).create().show();
            }
        });

        btngoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                finish();
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    // 현재 파이어베이스에서 Cloud folder delete 지원 안함
//                    pathReference = mStorageRef.child(user.getUid());
//                    pathReference.delete();
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YEDAS/";
                    File root = new File(path);
                    if(root.exists()){
                        root.delete();
                    }
                    fDatabase = FirebaseDatabase.getInstance().getReference();
                    fRef = fDatabase.child("Files").child(user.getUid());
                    fRef.removeValue();
                    myRef = mDatabase.child("User").child(user.getUid());
                    myRef.removeValue();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"유저가 삭제 되었습니다",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


}
