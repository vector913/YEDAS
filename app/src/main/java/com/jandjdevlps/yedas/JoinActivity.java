package com.jandjdevlps.yedas;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class JoinActivity extends AppCompatActivity {
    EditText input_name,input_email,input_pswd,input_pswdch;
    TextView verif_t,descriptor;
    Spinner dept, job;
    String emails;
    String password;
    String namedat;
    String deptdat;
    String jobdat;
    String[] deptitems = new String[]{"예배부","음영부","상례부","디지털미디어부","혼례부","교육부","홍보부","봉사부","선교부","복지부","교우부","새가족부","서무부","재정부","차량관리부","시설관리부","영은설악동산관리부","노인학교"};
    String[] jobitems = new String[]{"목사","장로","안수집사","권사","서리집사","청년"};
    Button set_dat,cancel;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    DatabaseReference mRef;
    private DatabaseReference fDatabase;
    private DatabaseReference fRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        input_name = findViewById(R.id.join_input_name);
        input_email =findViewById(R.id.join_input_email);
        input_pswd = findViewById(R.id.join_input_pswd);
        input_pswdch = findViewById(R.id.join_input_pswdch);
        dept = findViewById(R.id.join_input_dept);
        job = findViewById(R.id.join_input_job);

        descriptor = findViewById(R.id.join_descriptor);
        verif_t = findViewById(R.id.join_verification);
        cancel = findViewById(R.id.join_cancel_btn);
        set_dat = findViewById(R.id.join_change_btn);

        //user = FirebaseAuth.getInstance().getCurrentUser();
        //mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRef = mDatabase.child("User");

//        if(user.isEmailVerified()){
//            verif_t.setText("인증되어있습니다.");
//            verif_t.setTextColor(Color.parseColor("#4CAF50"));
//        }

        descriptor.setText("이 앱은 서울시 영등포구에 있는 영은교회에 사용될 전자 결재용입니다.\n" +
                "다를 사용자 분들은 통보 없이 삭제될 수 있음을 미리 고지합니다.\n" +
                "반드시 가입전에 이전 화면에서 등록하신 이메일로 인증부터 해주신후 진행해주시면 감사드리겠습니다.\n" +
                "사용중에 불편을 느끼시거나 이상이 발생할경우 개발자 이메일을 통해 문의해주시면 됩니다.");
        descriptor.setTextColor(Color.parseColor("#000000"));

        ArrayAdapter<String> adapter_dept = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,deptitems);
        ArrayAdapter<String> adapter_job = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,jobitems);
        adapter_dept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_job.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(adapter_dept);
        job.setAdapter(adapter_job);

    }

    @Override
    protected void onStart(){
        super.onStart();

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        mAuth = FirebaseAuth.getInstance();

        dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptdat = dept.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deptdat = null;
            }
        });
        job.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jobdat = job.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                jobdat = null;
            }
        });


        set_dat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_email.getText().toString()==null){
                    Toast.makeText(getApplicationContext(),"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(input_name.getText().toString() == null){
                    Toast.makeText(getApplicationContext(),"성함을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(deptdat == null){
                    Toast.makeText(getApplicationContext(),"부서를 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(jobdat == null){
                    Toast.makeText(getApplicationContext(),"직분을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(input_pswd.getText().toString().length()<8){
                    Toast.makeText(getApplicationContext(),"비밀번호의 길이는 최소 8자 이상입니다.",Toast.LENGTH_SHORT).show();
                }else if(!input_pswd.getText().toString().equals(input_pswdch.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"작성하신 비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth = FirebaseAuth.getInstance();
                    emails = input_email.getText().toString();
                    namedat = input_name.getText().toString();
                    password = input_pswd.getText().toString();
                    firebaseAuth.createUserWithEmailAndPassword(emails,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(getApplicationContext(),"해당 이메일로 인증내용이 전송되었습니다. \n승인을 하신후 계속 진행해주세요",Toast.LENGTH_LONG).show();
                                        User userdat = new User(namedat,emails,deptdat,jobdat,user.isEmailVerified());
                                        mRef.child(user.getUid()).setValue(userdat);
//                                        mRef.child(user.getUid()).child("isEmailVerified").setValue(user.isEmailVerified());
                                        String[] dat1 = emails.split("@",2);
                                        String url = dat1[1];
                                        String net = "https://www."+url;
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(net)));
                                        Intent set_dat = new Intent(getApplicationContext(),MainViewActivity.class);
                                        //mRef.child(user.getUid());
                                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                        final File root = new File(path,"/YEDAS/");
                                        if(!root.exists()){
                                            root.mkdirs();
                                        }
                                        startActivity(set_dat);
                                        Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"User 생성에 실패했습니다. \n관리자에게 문의해주세요.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    }
                }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"회원가입을 취소하였습니다.",Toast.LENGTH_SHORT).show();
//                user = FirebaseAuth.getInstance().getCurrentUser();
//               if(user!=null) {
//                   user.delete();
//               }
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }
}
