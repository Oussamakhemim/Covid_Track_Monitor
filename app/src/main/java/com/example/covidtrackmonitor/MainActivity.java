package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    PermissionManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null){

            setContentView(R.layout.activity_main);
            manager=new PermissionManager() {};
            manager.checkAndRequestPermissions(this);

        }else{

            Intent intent=new Intent(MainActivity.this,NaviguationActivity.class);
            startActivity(intent);
            finish();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> permissions_rejected =manager.getStatus().get(0).denied;
        if(permissions_rejected.isEmpty()){
            Toast.makeText(getApplicationContext(),"Toutes les permissions sont données",Toast.LENGTH_SHORT).show();
        }
    }

    public void login(View v) {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}