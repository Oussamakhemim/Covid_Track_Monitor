package com.example.covidtrackmonitor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {


    String email,password;
    EditText e2_name;
    CircleImageView circleImageView;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        e2_name=(EditText) findViewById(R.id.editTextTextPersonName);
    circleImageView =(CircleImageView) findViewById(R.id.circleImageView);
        Intent intent=getIntent();
        if(intent !=null){
            email= intent.getStringExtra("email");
            password=intent.getStringExtra("password");
        }

    }

    public void generateCode(View v){
        Date myDate=new Date();
        SimpleDateFormat myformat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        String date=myformat.format(myDate);
        Random r= new Random();
        //generer un entier aleatoire
        int n=100000+r.nextInt(900000);
        String code=String.valueOf(n);

        if(resultUri!=null){
            Intent intent=new Intent(NameActivity.this,InviteCodeActivity.class);
                intent.putExtra("name",e2_name.getText().toString());
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                intent.putExtra("date",date);
                intent.putExtra("infecte","false");
                intent.putExtra("code",code);
                intent.putExtra("imageUri",resultUri);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(getApplicationContext(),"veuillez selectioner une photo de profil",Toast.LENGTH_SHORT).show();

        }

    }

    public void selectImage(View v){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,12);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            //// start picker to get image for cropping and then use the image in cropping activity using the library Android Image Cropper
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        //Override onActivityResult method in activity to get crop result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri); //show photo in miniature after cropping(afficher la photo aprés le décopage)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }
}