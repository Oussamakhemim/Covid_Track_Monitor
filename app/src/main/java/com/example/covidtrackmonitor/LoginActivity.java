package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText e1,e2 ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e1= (EditText) findViewById(R.id.editTextTextEmailAddress);
        e2= (EditText) findViewById(R.id.editTextTextPassword);
        auth=FirebaseAuth.getInstance();
    }

    public void login(View v){
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        auth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Connexion avec sucées",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,NaviguationActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}