package com.namrahrasool.i170018_i170010_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCourseActivity extends AppCompatActivity {
    ImageView back,save,logout;
    EditText name,description;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);
        mAuth = FirebaseAuth.getInstance();
        name=(EditText)findViewById(R.id.name);
        description=(EditText)findViewById(R.id.description);
        save=findViewById(R.id.save);
        logout=findViewById(R.id.logout);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddCourseActivity.this,CoursesActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(CreateSchedule.this,TaughtCourses.class);
                //startActivity(intent);
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(AddCourseActivity.this);
                builder.setMessage("Do you want to Logout ?");
                builder.setTitle("Logout !");
                builder.setCancelable(false);
                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent=new Intent(AddCourseActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                builder
                        .setNegativeButton(
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
        firebaseUser= mAuth.getCurrentUser();
        userid=firebaseUser.getUid();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course course=new Course(name.getText().toString(),description.getText().toString());
                reference= FirebaseDatabase.getInstance().getReference("courses").child(userid).child(course.getName());
                reference.keepSynced(true);
                reference.setValue(course).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(AddCourseActivity.this, CoursesActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(AddCourseActivity.this,"You can't register with this email or password!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //Toast.makeText(getApplicationContext(), "Course created successfully!",
                //       Toast.LENGTH_LONG).show();
                //Intent intent=new Intent(AddCourseActivity.this,CoursesActivity.class);
                //startActivity(intent);
            }
        });
    }
}