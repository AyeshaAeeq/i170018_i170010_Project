package com.namrahrasool.i170018_i170010_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText name,username,email,password;
    ImageView createAccount,back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        name=(EditText)findViewById(R.id.name);
        username=(EditText)findViewById(R.id.username);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        back=findViewById(R.id.back);
        createAccount=findViewById(R.id.createAccount);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this,email.getText().toString()+" and pass:"+password.getText().toString(),Toast.LENGTH_LONG).show();
                mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignupActivity.this,"Successfully created user:"+email.getText().toString()+" and pass:"+password.getText().toString(),Toast.LENGTH_LONG).show();
                                    Toast.makeText(SignupActivity.this,mAuth.getCurrentUser().getUid()+"",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(SignupActivity.this, CoursesActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this,"Failed to create user",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
