package com.namrahrasool.i170018_i170010_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SectionsActivity extends AppCompatActivity {
    MyRecyclerViewAdapter adapter;
    private FloatingActionButton profile;
    private FloatingActionButton add;
    private FloatingActionButton editSection;
    private ImageView back;
    static String course;
    String userid;
    Intent intent;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser fuser;
    List<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);
        intent = getIntent();
        course = intent.getStringExtra("course");
        Toast.makeText(SectionsActivity.this, course, Toast.LENGTH_LONG).show();
        profile=(FloatingActionButton)findViewById(R.id.profile);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SectionsActivity.this,CoursesActivity.class);
                startActivity(intent);
            }
        });
        add=(FloatingActionButton)findViewById(R.id.addSection);
        editSection=(FloatingActionButton)findViewById(R.id.editSection);
        editSection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SectionsActivity.this, EditSectionActivity.class);
                intent.putExtra("course",course);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SectionsActivity.this,AddSectionActivity.class);
                intent.putExtra("course",course);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(SectionsActivity.this);
                builder.setMessage("Do you want to Logout ?");
                builder.setTitle("Logout !");
                builder.setCancelable(false);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent=new Intent(SectionsActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        data= new ArrayList<String>();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        userid=fuser.getUid();
        database= FirebaseDatabase.getInstance();
        reference=database.getReference("sections").child(userid).child(course);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Section sec=snapshot.getValue(Section.class);
                data.add(sec.getName());
                //adapter.setContacts(data);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //data.add("A");
        //data.add("B");
        //data.add("C");
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(data, this,"sections");
        adapter.setCourseName(course);
        recyclerView.setAdapter(adapter);
    }
    public void profileActivity(){

    }
}
