package com.example.yedas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
        Toolbar toolbar;
//        FloatingActionButton fab;
        FirebaseAuth firebaseAuth;
        DrawerLayout drawer;
        NavigationView navigationView;
        ActionBarDrawerToggle toggle;

        TextView user_name;
        TextView user_email;
        ListView listView;

        private DatabaseReference mDatabase;
        private DatabaseReference myRef;
        private DatabaseReference fDatabase;
        private DatabaseReference fRef;

        String filename;
        String sender;
        String date;
        String type;
        String descript;
        int decision;

         ListViewAdapter adapter;
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView= findViewById(R.id.documents);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

//        fab = findViewById(R.id.fab);
        View headerView = navigationView.getHeaderView(0);
        user_name = headerView.findViewById(R.id.user_name);
        user_email = headerView.findViewById(R.id.user_email);
        filename = "is null";
        sender = "no none";

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myRef  = mDatabase.child("User");

        fDatabase = FirebaseDatabase.getInstance().getReference();
        fRef = fDatabase.child("Files");

        final FirebaseUser user  = firebaseAuth.getCurrentUser();
        final ArrayList<ListViewitem> data=new ArrayList<>();
        final ArrayList<Document> doc_obj = new ArrayList<>();
        adapter = new ListViewAdapter(this,R.layout.document_item,data);

        if(user!=null) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userd = dataSnapshot.child(user.getUid()).getValue(User.class);
                    if (userd != null) {
                        user_name.setText(userd.getUsername());
                        user_email.setText(userd.getEmail());
                    }else{
                        user_name.setText(user.getDisplayName());
                        user_email.setText(user.getEmail());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            fRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.child(user.getUid()).getChildren()) {
                        Document doc = ds.getValue(Document.class);
                        date = doc.getDate();
                        descript = doc.getDescript();
                        decision = doc.getDecision();
                        filename = doc.getfilename();
                        sender = doc.getSender();
                        type = doc.getType();
                        if(decision<0){
                             if(decision==-2){
                            Uri uri_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification n = new Notification.Builder(getApplicationContext())
                                    .setContentTitle("새 결재 파일이 도착했습니다.")
                                    .setContentText("작성자 : "+sender+" 파일 이름 : "+filename)
                                    .setSmallIcon(R.drawable.ic_menu_manage)
                                    .setSound(uri_sound)
                                    .build();
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(0,n);
                        }
                        Document new_obj = new Document(filename,sender,type,date,descript,decision);
                        ListViewitem u = new ListViewitem(filename, sender);
                        data.add(u);
                        doc_obj.add(new_obj);
                        }
                    }
                    if(data.isEmpty()){
                        filename = "파일이 전송된 것이 없습니다.";
                        sender = " ";
                        data.add(new ListViewitem(filename,sender));
                        filename="";
                    }
                    listView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    filename = "파일이 전송된 것이 없습니다.";
                    sender = " ";
                    data.add(new ListViewitem(filename,sender));
                    listView.setAdapter(adapter);
                }
            });

        }else{
            startActivity(new Intent(getApplicationContext(), LoadingScreenActivity.class));
            finish();
        }
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "앞으로 이버튼을 누르게 된다면\n 데이터 전송 예정", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent doclist = new Intent(getApplicationContext(), DocumentViewActivity.class);
            if(filename.equals("")||doc_obj.isEmpty()){
                    Toast.makeText(getApplicationContext(),"접근할 파일이 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
            }else {
            filename = doc_obj.get(position).getfilename();
            date =doc_obj.get(position).getDate();
            sender=doc_obj.get(position).getSender();
            type =doc_obj.get(position).getType();
            descript =doc_obj.get(position).getDescript();

            doclist.putExtra("file_name",filename);
            doclist.putExtra("doc_date",date);
            doclist.putExtra("doc_dat",filename);
            doclist.putExtra("writer_dat",sender);
            doclist.putExtra("type",type);
            doclist.putExtra("decryption",descript);
            decision = -1;
            doclist.putExtra("decision",decision);

                startActivity(doclist);
                finish();
            }
            }
        });
       // listView.setAdapter(adapter);
    }

        @Override
        public void onBackPressed () {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /*     @Override
        public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tools) {
           startActivity(new Intent(getApplicationContext(), MainActivity.class));
           this.finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
