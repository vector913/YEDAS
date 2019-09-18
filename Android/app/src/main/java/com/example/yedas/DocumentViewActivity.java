package com.example.yedas;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DocumentViewActivity extends AppCompatActivity {
    TextView doc_id;
    TextView doc_sender;
    TextView doc_descript;
    TextView doc_file_name;
    Button confirm_b;
    Button resend_b;
    Button decline_b;
    String doc_dat,writer_dat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_view);

        doc_id = findViewById(R.id.document_id);
        doc_sender = findViewById(R.id.document_sender);
        doc_descript = findViewById(R.id.document_descript);
        doc_file_name = findViewById(R.id.document_file_name);

        confirm_b = findViewById(R.id.send_confirm);
        resend_b = findViewById(R.id.send_re_check);
        decline_b = findViewById(R.id.send_decline);

        confirm_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "결재가 승인 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        resend_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "서류가 재요청 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        decline_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "결재가 거부 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        doc_dat = getIntent().getStringExtra("doc_dat");
        writer_dat = getIntent().getStringExtra("writer_dat");
        doc_id.setText(doc_dat);
        doc_sender.setText(writer_dat);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("문서종료")
                .setMessage("결재 없이 뒤로 가시겠습니까?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        DocumentViewActivity.super.onBackPressed();
                        Toast.makeText(getApplicationContext(),"결재가 취소되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }
}
