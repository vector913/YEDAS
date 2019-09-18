package com.example.yedas;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class NewPassActivity extends AppCompatActivity {
    EditText email;
    Button btnNewPass;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpass_activity);
        email = findViewById(R.id.newpswdEmail);
        btnNewPass = findViewById(R.id.sendnewpswd);

        firebaseAuth = FirebaseAuth.getInstance();

        btnNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = email.getText().toString();

                if(TextUtils.isEmpty(emails)){
                    Toast.makeText(getApplicationContext(),"이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(emails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"당신의 이메일로 비밀번호가 전송되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"이메일 전송 실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}
