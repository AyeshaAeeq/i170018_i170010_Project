package com.namrahrasool.i170018_i170010_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class CoursesActivity extends AppCompatActivity {
    MyRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton profile;
    private FloatingActionButton add;
    private FloatingActionButton createSchedule;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser fuser;
    String userid;
    static List<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        profile=(FloatingActionButton) findViewById(R.id.profile);
        add=(FloatingActionButton)findViewById(R.id.addCourse);
        createSchedule=(FloatingActionButton)findViewById(R.id.createSchedule);
        createSchedule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CoursesActivity.this,CreateScheduleActivity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CoursesActivity.this,AddCourseActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(CoursesActivity.this);
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
                                Intent intent=new Intent(CoursesActivity.this,MainActivity.class);
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
        reference=database.getReference("courses").child(userid);
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Course currentCourse=snapshot.getValue(Course.class);
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                String id=fuser.getUid();
                data.add(currentCourse.getName());
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
        //data.add("Computer Architecture");
        ////data.add("Data Minning");
        //data.add("Human Computer Interaction");
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv);
        int numberOfColumns = 2;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter(data, this,"courses");
        recyclerView.setAdapter(adapter);
    }
    public void profileActivity(){
        Intent intent = new Intent(this,SectionsActivity.class);
        startActivity(intent);
    }
}