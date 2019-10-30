package com.jandjdevlps.yedas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ApprovalActivity extends AppCompatActivity {
    private Button btnClear, btnSave, btnCancel ,btnBack;
    private File file;
    private LinearLayout canvasLL;
    private View view;
    private signature mSignature;
    private Bitmap bitmap;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/YEDAS/";
    String pic_name;
    String descript;
    String StoredPath;
    String tmp;
    private DatabaseReference fDatabase;
    private DatabaseReference fRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approval_activity);

        pic_name = getIntent().getStringExtra("filename");
        StoredPath = DIRECTORY + pic_name + ".png";

        canvasLL = findViewById(R.id.canvasLL);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        final byte[] bytes = getIntent().getByteArrayExtra("Image");
        Bitmap bits = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Drawable drawable = new BitmapDrawable(bits);
        mSignature.setBackground(drawable);
        // Dynamically generating Layout through java code
        canvasLL.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        btnClear =  findViewById(R.id.btn_clear);
        btnSave = findViewById(R.id.send_confirm);
        btnBack = findViewById(R.id.send_re_check);
        btnCancel = findViewById(R.id.send_decline);
        fDatabase = FirebaseDatabase.getInstance().getReference();
        fRef = fDatabase.child("Files");
        tmp = getIntent().getStringExtra("filename");
        view = canvasLL;

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setDrawingCacheEnabled(true);
                mSignature.save(view,StoredPath);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl("gs://yedas-e5423.appspot.com");
                StorageReference mountimg = storageReference.child(user.getUid()).child(pic_name+".png");
                try {
                    fRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.child(user.getUid()).getChildren()) {
                                Document doc = ds.getValue(Document.class);
                                assert doc != null;
                                if(doc.getfilename().equals(tmp)){
                                   fRef = ds.getRef();
                                   break;
                                }
                                fRef.child("decision").setValue(1);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                    byte[] bytef = stream.toByteArray();
                    UploadTask uploadTask = mountimg.putBytes(bytef);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"승인전송이 거부 되었습니다.\n관리자에게 문의하세요!",Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(),"결재가 승인되었습니다.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainViewActivity.class));
                            finish();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ApprovalActivity.this)
                        .setTitle("결재 서류창 종료")
                        .setMessage("결재 없이 종료 하시겠습니까?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                startActivity(new Intent(getApplicationContext(),MainViewActivity.class));
                                finish();
                                Toast.makeText(getApplicationContext(), "결재 없이 종료합니다.",Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            final String[] items = {"내용 오류", "오타 발견", "내용 수정 요청", "기타 사유"};

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ApprovalActivity.this)
                        .setTitle("결재 거절 사유")
                        .setIcon(R.drawable.ic_menu_manage)
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                descript = items[item];
                                if(descript.equals("기타 사유")){
                                    Toast.makeText(getApplicationContext(),"해당 사유는 결재작성자에게 따로 전달해주세요.", Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton("거절 사유 전송",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                fRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.child(user.getUid()).getChildren()) {
                                            Document doc = ds.getValue(Document.class);
                                            assert doc != null;
                                            if(doc.getfilename().equals(tmp)){
                                               fRef = ds.getRef();
                                                break;
                                            }
                                        }
                                        fRef.child("decision").setValue(0);
                                        fRef.getRef().child("descript").setValue(descript);
                                        startActivity(new Intent(getApplicationContext(),MainViewActivity.class));
                                        finish();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                               // Toast.makeText(ApprovalActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(ApprovalActivity.this,"다시 결정해주세요",Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();



                //Toast.makeText(getApplicationContext(), "결재를 거절하였습니다.\n기능생성중 ",Toast.LENGTH_SHORT).show();

            }
        });

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(canvasLL.getWidth(), canvasLL.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("문서종료")
                .setMessage("결재 없이 처음으로 가시겠습니까?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ApprovalActivity.super.onBackPressed();
                        startActivity(new Intent(ApprovalActivity.this,MainViewActivity.class));
                        Toast.makeText(getApplicationContext(),"결재가 취소되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).create().show();
    }
}
