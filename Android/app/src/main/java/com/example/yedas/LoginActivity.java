package com.example.yedas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    EditText loginEmail,loginPassword;
    Button loginButton,registerButton;
    TextView newPassButton;
    private static final int RC_SIGN_IN = 9001;
    private static String TAG = "LoginActivity";
    private SignInButton signInButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword =  findViewById(R.id.login_pswd);
        loginButton =  findViewById(R.id.login_btn);
        registerButton =  findViewById(R.id.register_btn);
        signInButton = findViewById(R.id.gsign_in_button);
        newPassButton = findViewById(R.id.find_pswd);
        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }else if(password.length()<4){
                    Toast.makeText(getApplicationContext(),"비밀번호는 4자리 이상이어야합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "signInWithEmail:sendVerification");
                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.sendEmailVerification();
                                    Toast.makeText(getApplicationContext(),"해당 이메일로 인증내용이 전송되었습니다. \n승인을 하신후 로그인해주세요.",Toast.LENGTH_LONG).show();
                                    //startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                                    //finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"이미 등록된 유저입니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        newPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NewPassActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 4) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 4자리 이상이어야합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                if (task.isSuccessful()&&user.isEmailVerified()) {
                                    Log.d(TAG, "LogInWithEmail:success");
                                    startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "E-mail 이나 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
            finish();
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
            finish();
        }

    }

    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), MainViewActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"인증오류",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"연결실패",Toast.LENGTH_SHORT).show();
    }
}
