package com.jandjdevlps.yedas;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.jandjdevlps.yedas.lib.StringLib;

public class UserInfoChangeActivity extends AppCompatActivity {

    EditText input_name; // 사용자 이름
    TextView verif_t;    // 인증 결과 표시 View
    Spinner  dept, job;  // 맡은 부서, 및 담당 직부 표시 스피너
    String namedat;      // 이름 저장용 변수
    String deptdat;      // 부서 저장용 변수
    String jobdat;       // 직분 저장용 변수
    //Note 각 영은교회에서 사용하는 스피너 데이터
    String[] deptitems = new String[]{"예배부","음영부","상례부","디지털미디어부","혼례부","교육부","홍보부","봉사부","선교부","복지부","교우부","새가족부","서무부","재정부","차량관리부","시설관리부","영은설악동산관리부","노인학교"};
    String[] jobitems = new String[]{"목사","장로","안수집사","권사","서리집사","청년"};
    Button  set_dat,cancel; // 사용자 데이터 적용 혹은 취소
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRef = mDatabase.child("User");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user_info);

        //Note View 설정
        input_name = findViewById(R.id.input_name);
        dept = findViewById(R.id.input_dept);
        job = findViewById(R.id.input_job);

        verif_t = findViewById(R.id.verification);
        cancel = findViewById(R.id.request_cancel_btn);
        set_dat = findViewById(R.id.request_change_btn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        //Note 이메일 검증 여부
        if(user.isEmailVerified()){
            verif_t.setText("인증완료");
            verif_t.setTextColor(Color.parseColor("#4CAF50"));
        }

        ArrayAdapter<String> adapter_dept = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,deptitems);
        ArrayAdapter<String> adapter_job = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,jobitems);
        adapter_dept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_job.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(adapter_dept);
        job.setAdapter(adapter_job);

        //note Firebase 객체
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        setEventListener();
    }

    public void setEventListener()
    {
        //Note 부서 선택 Listener
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
        //Note 직분 선택 Listener
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

                namedat = input_name.getText().toString();

                if(StringLib.isNullorEmpty(namedat)){
                    Toast.makeText(getApplicationContext(),"성함을 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(StringLib.isNullorEmpty(deptdat)){
                    Toast.makeText(getApplicationContext(),"부서를 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else if(StringLib.isNullorEmpty(jobdat)){
                    Toast.makeText(getApplicationContext(),"직분을 선택해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    String emails = user.getEmail();

                    if(user == null)
                    {
                        //Note 객체 다시 가져오기 재시도.
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user == null)
                        {
                            Toast.makeText(UserInfoChangeActivity.this,"정보를 수정할 수 없는 상태입니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    User userdat = new User(namedat,emails,deptdat,jobdat, user.isEmailVerified());
                    mRef.child(user.getUid()).setValue(userdat);

                    setResult(RESULT_OK);

                    Toast.makeText(getApplicationContext(),"정보수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"정보수정을 취소하였습니다.",Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        verif_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.isEmailVerified()) {
                    user.sendEmailVerification();
                    String emails = user.getEmail();
                    if(!StringLib.isNullorEmpty(emails))
                    {
                        String[] dat1 = emails.split("@",2);
                        String url = dat1[1];
                        String net = "https://www."+url;

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(net)));
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"이미 인증되어 있습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
