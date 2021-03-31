package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CodeActivity extends AppCompatActivity {

    String code;
    TextView t1;
    FirebaseAuth auth;
    DatabaseReference reference;
    Button boutt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        t1=(TextView) findViewById(R.id.codeinv);
        auth=FirebaseAuth.getInstance();
        Intent intent=getIntent();

        boutt= (Button) findViewById(R.id.okcode);
        boutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
                finish();
            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("users"); //pointer sur la table users
        reference.child(auth.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    code=snapshot.child("code").getValue(String.class);
                    t1.setText(code);
                    Toast.makeText(CodeActivity.this,"le code est  : "+code ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void retour()
    {
        Intent intent=new Intent(CodeActivity.this,NaviguationActivity.class);
        startActivity(intent);
        finish();
    }
}