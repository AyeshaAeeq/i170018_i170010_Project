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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ImageView login,signup,back;
    EditText password,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Successfully signed in:" + email.getText().toString() + " and pass:" + password.getText().toString(), Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(LoginActivity.this, CoursesActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this,"Failed to sign in: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(LoginActivity.this, CoursesActivity.class);
            startActivity(intent);
        }
    }
}