package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteCodeActivity extends AppCompatActivity {

    String name,email,password,date,infecte,code;
    Uri imageUri;
    TextView t1;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);
        t1=(TextView) findViewById(R.id.textView);
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        Intent intent=getIntent();

        reference= FirebaseDatabase.getInstance().getReference().child("users"); //pointer sur la table users
        if(intent !=null){
            name= intent.getStringExtra("name");
            email= intent.getStringExtra("email");
            password=intent.getStringExtra("password");
            infecte=intent.getStringExtra("infecte");
            code=intent.getStringExtra("code");
            imageUri=intent.getParcelableExtra("imageUri");
        }

        t1.setText(code);
    }

    public void registerUser(View v){
        progressDialog.setMessage("Veuillez patienter s'il vous plaît, compte en cours de création");
        progressDialog.show();
        // Write a message to the database
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //insert data in real time database.
                            user=auth.getCurrentUser();
                            CreateUser createUser=new CreateUser(name,email,password,code,"false","na","na",String.valueOf(imageUri),user.getUid());

                            userId=user.getUid();

                            reference.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Utilisateur ajouté avec sucées",Toast.LENGTH_SHORT).show();
                                                finish();
                                                Intent intent=new Intent(InviteCodeActivity.this,NaviguationActivity.class);
                                                startActivity(intent);

                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Impossible d'ajouter l'utilisateur",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }
                });
    }
}