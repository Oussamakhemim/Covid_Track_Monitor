package com.example.covidtrackmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {


    String email;
    EditText e2_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        e2_password=(EditText) findViewById(R.id.editTextTextPassword2);

        Intent intent=getIntent();
        if (intent!=null){
            email=intent.getStringExtra("email");
        }
    }

    public void goToNameAvatarActivity(View v){
        if(e2_password.getText().toString().length()>6){
            Intent intent= new Intent(PasswordActivity.this,NameActivity.class);
            intent.putExtra("email",email);
            intent.putExtra("password",e2_password.getText().toString());
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(),"le mot de passe doit contenir au moins 6 caractères",Toast.LENGTH_SHORT).show();
        }

    }
}