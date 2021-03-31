package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText e2_email;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e2_email= (EditText) findViewById(R.id.editTextTextEmailAddress2);
        auth=FirebaseAuth.getInstance();
        dialog= new ProgressDialog(this);

    }

    public void goToPassword(View V) {
        dialog.setMessage("vérification de l'email en cours");
        dialog.show();
        //verifier si l'email existe déja ou pas
        auth.fetchSignInMethodsForEmail(e2_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        if(task.isSuccessful()){
                            dialog.dismiss();
                            boolean verif =!task.getResult().getSignInMethods().isEmpty();
                            if(!verif)
                            {
                                //email n'existe pas, alors on peut creer cet email et l'associer a l'utilisateur
                                Intent intent=new Intent(RegisterActivity.this,PasswordActivity.class);
                                intent.putExtra("email",e2_email.getText().toString());
                                startActivity(intent);
                                finish();

                            }else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"l'email que vous venez de tappez est déja associé a un compte",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}