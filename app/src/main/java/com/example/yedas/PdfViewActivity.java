package com.example.yedas;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;


public class PdfViewActivity extends AppCompatActivity {
    ImageView imageViewPdf;
    TextView tv;
    String filename;
    Button close;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view_layout);

        filename = getIntent().getStringExtra("files");
        tv= findViewById(R.id.file_name_pdf);
        close = findViewById(R.id.button_pre_doc);
        imageViewPdf = findViewById(R.id.pdf_image);
        tv.setText(filename);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File root = new File(path,"/YEDAS/");
        File localFile = new File(root,filename);
        if(!localFile.exists()){
            Toast.makeText(getApplicationContext(),"파일이 내장메모리에 존재하지 않습니다.\n관리자에게 문의바랍니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        Uri paths = FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",localFile);
        Intent intent = new Intent(Intent.ACTION_VIEW,paths);
        intent.setDataAndType(paths,"application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try{
            startActivity(intent);
//            Toast.makeText(getApplicationContext(),"파일 : "+filename+"\n파일 디렉토리 :"+localFile,Toast.LENGTH_SHORT).show();
            finish();
        }catch(ActivityNotFoundException ex){
            Toast.makeText(this,"PDF뷰어가 존재하지 않습니다. 관련 앱을 설치해주세요!\n혹은 관리자에게 문의바랍니다.",Toast.LENGTH_SHORT).show();
        }

        }

}
