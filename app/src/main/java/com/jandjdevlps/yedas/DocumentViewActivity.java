package com.jandjdevlps.yedas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

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
    String filename,type,tmp,sender,date,senderuid;
    int decision;
    ArrayList<Bitmap> bitmap;
    private DatabaseReference fDatabase;
    private DatabaseReference fRef;
    private DatabaseReference tRef;
    private DatabaseReference sfRef;
    private DatabaseReference uRef;
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
        date = getIntent().getStringExtra("doc_date");
        doc_date.setText(date);
        doc_id.setText(getIntent().getStringExtra("title"));
        sender = getIntent().getStringExtra("writer_dat");
        doc_sender.setText(sender);
        sender = doc_sender.getText().toString();
        doc_descript.setText(getIntent().getStringExtra("decryption"));
        type = getIntent().getStringExtra("type");
        filename = getIntent().getStringExtra("doc_dat");
        tmp = filename;
        filename = filename+"."+type;
        doc_file_name.setText(filename);
        decision = getIntent().getIntExtra("decision",-1);
        assert user != null;
        fDatabase = FirebaseDatabase.getInstance().getReference();
        fRef = fDatabase.child("Files");
        sfRef = fDatabase.child("SendFiles");
        uRef = fDatabase.child("User");

        if(decision==-2) {
            fRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.child(user.getUid()).getChildren()) {
                        Document doc = ds.getValue(Document.class);
                        assert doc != null;
                        if (doc.getfilename().equals(tmp)&&doc.getDate().equals(date)) {
                            tRef = ds.getRef();
                            tRef.child("decision").setValue(-1);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            uRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    System.out.println("key2 :" + dataSnapshot.getKey());
                    for (DataSnapshot un :dataSnapshot.getChildren()) {
                        User who = un.getValue(User.class);
                        senderuid = who.getStrUsername();
                        // System.out.println("name :"+sender+" un.getkey() : "+un.getKey());
                        if(senderuid.equals(sender)){
                            senderuid = un.getKey();
                            break;
                        }
                    }
                    // System.out.println("name :" + senderuid);
                    sfRef.child(senderuid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int check = 0;
//                                            System.out.println("tmp : " + tmp);
//                                            System.out.println("date : " + date);
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                Document doc = ds.getValue(Document.class);
//                                                System.out.println("filename :" + doc.getfilename());
//                                                System.out.println("decision :"+ doc.getDecision());
//                                                System.out.println("Date : " +doc.getDate());
//                                                System.out.println("Ref  : "+ds.getRef());
                                assert doc != null;
                                if(doc.getfilename().equals(tmp)&&doc.getDecision()<0&&doc.getDate().equals(date)){
                                    sfRef = ds.getRef();
                                    // System.out.println("Ref:" + sfRef.getRef().toString());
                                    sfRef.child("decision").setValue(-1);
                                    check =1;
                                    break;
                                }
                            }
                            if(check == 0){
                                Toast.makeText(getApplicationContext(),"데이터베이스 기록에 실패했습니다. 관리자에게 문의바랍니다.",Toast.LENGTH_LONG).show();
                            }
//                                            startActivity(new Intent(getApplicationContext(),MainViewActivity.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        pathReference = mStorageRef.child(user.getUid()).child(filename);
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
                                    Intent intent = new Intent(DocumentViewActivity.this,ApprovalActivity.class);
                                    Bitmap bits = bitmap.get(0);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bits.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    intent.putExtra("date2",date);
                                    intent.putExtra("filename", tmp);
                                    intent.putExtra("names2",sender);
                                    intent.putExtra("Image", bytes);
                                    yourProgressDialog.dismiss();
                                    finish();
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
                            Intent intent = new Intent(getApplicationContext(), ApprovalActivity.class);
                            Bitmap bits = bitmap.get(0);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bits.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bytes = stream.toByteArray();
                            intent.putExtra("date2",date);
                            intent.putExtra("filename", tmp);
                            intent.putExtra("names2",sender);
                            intent.putExtra("Image", bytes);
                            finish();
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
                startActivity(new Intent(DocumentViewActivity.this,MainViewActivity.class));
                Toast.makeText(getApplicationContext(),"결재가 취소되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });


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
                        startActivity(new Intent(DocumentViewActivity.this,MainViewActivity.class));
                        Toast.makeText(getApplicationContext(),"결재가 취소되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
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
