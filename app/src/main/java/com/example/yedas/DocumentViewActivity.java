package com.example.yedas;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.internal.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;


public class DocumentViewActivity extends AppCompatActivity {
    TextView doc_id;
    TextView doc_sender;
    TextView doc_descript;
    TextView doc_date;
    TextView doc_file_name;
    Button confirm_b;
    Button decline_b;

    FirebaseAuth firebaseAuth;
    String filename,type,tmp;
    ArrayList<Bitmap> bitmap;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageRef = storage.getReferenceFromUrl("gs://yedas-e5423.appspot.com");
    StorageReference pathReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_view);

        doc_id = findViewById(R.id.document_id);
        doc_sender = findViewById(R.id.document_sender);
        doc_date = findViewById(R.id.document_date);
        doc_descript = findViewById(R.id.document_descript);
        doc_file_name = findViewById(R.id.document_file_name);

        confirm_b = findViewById(R.id.goto_doc);
        decline_b = findViewById(R.id.cancel_back);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user  = firebaseAuth.getCurrentUser();


        confirm_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    final File root = new File(path,"/YEDAS/");
                    if(!root.exists()){
                        root.mkdirs();
                    }
                    final File localFile = new File(root,filename);
                    if(!localFile.exists()) {
                        final ProgressDialog yourProgressDialog = new ProgressDialog(DocumentViewActivity.this);
                        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                                try {
                                    bitmap = pdfToBitmap(localFile);
                                    Intent intent = new Intent(getApplicationContext(), DrawSignActivity.class);
                                    Bitmap bits = bitmap.get(0);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bits.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    intent.putExtra("filename", tmp);
                                    intent.putExtra("Image", bytes);
                                    yourProgressDialog.dismiss();
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                yourProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), filename + "이 존재 하지 않습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
                                Log.e("firebase ", ";local tem file not created  created " + localFile.toString());
                                Log.e("YEDAS ", ";check whether permission is granted" + localFile.toString());
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Toast.makeText(DocumentViewActivity.this, filename + "이 다운로드 중입니다.\n잠시만 기다려주세요!", Toast.LENGTH_SHORT).show();
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                yourProgressDialog.setMessage(filename+" 다운로드 중입니다 : " + ((int)progress) + "%...");
                                yourProgressDialog.show();
                            }
                        });

                    }else{
                        try {
                            bitmap = pdfToBitmap(localFile);
                            Intent intent = new Intent(getApplicationContext(), DrawSignActivity.class);
                            Bitmap bits = bitmap.get(0);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bits.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bytes = stream.toByteArray();

                            intent.putExtra("filename", tmp);
                            intent.putExtra("Image", bytes);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
               // Toast.makeText(getApplicationContext(), "결재가 승인 되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        decline_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "결재가 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        doc_date.setText(getIntent().getStringExtra("doc_date"));
        doc_id.setText(getIntent().getStringExtra("doc_dat"));
        doc_sender.setText(getIntent().getStringExtra("writer_dat"));
        type = getIntent().getStringExtra("type");
        filename = getIntent().getStringExtra("doc_dat")+"."+type;
        tmp = getIntent().getStringExtra("doc_dat");
        doc_file_name.setText(filename);
        assert user != null;
        pathReference = mStorageRef.child(user.getUid()).child(filename);
        doc_file_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    final File root = new File(path,"/YEDAS/");
                    if(!root.exists()){
                        root.mkdirs();
                    }
                    final File localFile = new File(root,filename);
                    if(!localFile.exists()) {
                        final ProgressDialog yourProgressDialog = new ProgressDialog(DocumentViewActivity.this);
                        pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                                Intent intent = new Intent(getApplicationContext(),PdfViewActivity.class);
                                intent.putExtra("files",filename);
                                yourProgressDialog.dismiss();
                                    startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                yourProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), filename + "이 존재 하지 않습니다. 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show();
                                Log.e("firebase ", ";local tem file not created  created " + localFile.toString());
                                Log.e("YEDAS ", ";check whether permission is granted" + localFile.toString());
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                yourProgressDialog.setMessage(filename+" 다운로드 중입니다 : " + ((int)progress) + "%...");
                                yourProgressDialog.show();
                            }
                        });
                    }else{
                        Intent intent = new Intent(getApplicationContext(),PdfViewActivity.class);
                        intent.putExtra("files",filename);
                        startActivity(intent);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        });
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

    private  ArrayList<Bitmap> pdfToBitmap(File pdfFile) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                // close the page
                page.close();

            }

            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmaps;

    }

}
