package com.example.yedas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoChangeActivity extends AppCompatActivity {
    EditText input_name;
    TextView verif_t;
    Spinner  dept, job;
    String namedat;
    String deptdat;
    String jobdat;
    String[] deptitems = new String[]{"예배부","음영부","상례부","디지털미디어부","혼례부","교육부","홍보부","봉사부","선교부","복지부","교우부","새가족부","서무부","재정부","차량관리부","시설관리부","영은설악동산관리부","노인학교"};
    String[] jobitems = new String[]{"목사","장로","안수집사","권사","서리집사","청년"};
    Button  set_dat,cancel;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user_info);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        input_name = findViewById(R.id.input_name);
        dept = findViewById(R.id.input_dept);
        job = findViewById(R.id.input_job);

        verif_t = findViewById(R.id.verification);
        cancel = findViewById(R.id.request_cancel_btn);
        set_dat = findViewById(R.id.request_change_btn);

        if(user.isEmailVerified()){
            verif_t.setText("인증이 완료 되었습니다.");
            verif_t.setTextColor(Color.parseColor("#4CAF50"));
        }


        ArrayAdapter<String> adapter_dept = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,deptitems);
        ArrayAdapter<String> adapter_job = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,jobitems);
        adapter_dept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_job.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(adapter_dept);
        job.setAdapter(adapter_job);

        namedat = input_name.getText().toString();



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
                jobdat = dept.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                jobdat = null;
            }
        });

        set_dat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namedat == null){
                    Toast.makeText(getApplicationContext(),"성함을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(deptdat == null){
                    Toast.makeText(getApplicationContext(),"부서를 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(jobdat == null){
                    Toast.makeText(getApplicationContext(),"직분을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent set_dat = new Intent(getApplicationContext(),MainActivity.class);
                    String emails = user.getEmail();
                    writeNewUser(namedat,emails,deptdat,jobdat);
                    set_dat.putExtra("name",namedat);
                    startActivity(set_dat);
                    Toast.makeText(getApplicationContext(),"정보수정이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void writeNewUser(String username, String email, String departemnt, String job){
        User duser = new User(username,email,departemnt,job);
        mDatabase.child("users").child(username).setValue(duser);
    }
}
