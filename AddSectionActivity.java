package com.namrahrasool.i170018_i170010_project;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class AddSectionActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE=1000;
    private static final int READ_REQUEST_CODE=42;
    ImageView back,save,logout,studentList2;
    Spinner courses;
    EditText section,studentList1;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userid,course;
    Intent intent;
    String id,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_section);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_STORAGE);
        }
        intent = getIntent();
        course = intent.getStringExtra("course");
        Toast.makeText(AddSectionActivity.this,course,Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        courses=(Spinner)findViewById(R.id.courses);
        String[] courseList = new String[]{course};
        ArrayAdapter<String> courseAdapter= new ArrayAdapter<>(getApplicationContext(),R.layout.color_spinner_layout,courseList);
        courseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        courses.setAdapter(courseAdapter);
        section=findViewById(R.id.section);
        studentList1=findViewById(R.id.studentlist1);
        studentList2=findViewById(R.id.studentlist2);
        back=findViewById(R.id.back);
        save=findViewById(R.id.save);
        logout=findViewById(R.id.logout);
        firebaseUser= mAuth.getCurrentUser();
        userid=firebaseUser.getUid();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Section sec=new Section(section.getText().toString(),course);
                reference= FirebaseDatabase.getInstance().getReference("sections").child(userid).child(course).child(sec.getName());
                reference.keepSynced(true);
                reference.setValue(sec).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(AddSectionActivity.this, SectionsActivity.class);
                            intent.putExtra("course",course);
                            startActivity(intent);
                        }else{
                            Toast.makeText(AddSectionActivity.this,"You can't register with this email or password!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //Toast.makeText(getApplicationContext(), "Section created successfully!",
                //        Toast.LENGTH_LONG).show();
                //Intent intent=new Intent(AddSectionActivity.this,SectionsActivity.class);
                //startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddSectionActivity.this,SectionsActivity.class);
                intent.putExtra("course",course);
                startActivity(intent);
            }
        });
        studentList2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(CreateSchedule.this,TaughtCourses.class);
                //startActivity(intent);
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(AddSectionActivity.this);
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
                                        Intent intent=new Intent(AddSectionActivity.this,MainActivity.class);
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
    }

    private String readText(String input){
        File file=new File(input);
        StringBuilder text=new StringBuilder();
        try{
            BufferedReader br=new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null){
                text.append(line);
                String[]tokens=line.split(",");
                store(tokens[0],tokens[1]);
                text.append("\n");
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return text.toString();
    }

    public void store(final String name,final String id){
        final String image;
        StorageReference storageRef= FirebaseStorage.getInstance().getReference();
        storageRef.child(name+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", name);
                hashMap.put("image", uri.toString());
                Toast.makeText(AddSectionActivity.this,name+id,Toast.LENGTH_LONG).show();
                firebaseUser= mAuth.getCurrentUser();
                userid=firebaseUser.getUid();
                reference= FirebaseDatabase.getInstance().getReference("students").child(userid).child(course).child(section.getText().toString()).child(id);
                reference.keepSynced(true);
                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddSectionActivity.this,"Students saved!!!",Toast.LENGTH_LONG).show();;
                        }else{
                            Toast.makeText(AddSectionActivity.this,"Something wrong!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }
    private void performFileSearch(){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==READ_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            if(data!=null){
                Uri uri=data.getData();
                String path=uri.getPath();
                path=path.substring(path.indexOf(":")+1);
                Toast.makeText(this,""+path,Toast.LENGTH_LONG).show();
                readText(path);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQUEST_STORAGE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted!!!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Permission not granted!!!",Toast.LENGTH_LONG).show();
            }
        }
    }
}