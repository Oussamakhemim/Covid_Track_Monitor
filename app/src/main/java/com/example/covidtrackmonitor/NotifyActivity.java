package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class NotifyActivity extends AppCompatActivity {

    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private DatabaseReference reference, circlereference;
    private Button b1,b2;
    private CreateUser createUser;
    private String memberid, username;
    private static final String ONESIGNAL_APP_ID = "fe6affc0-47ff-41ed-b15b-5b3b9de5ca69";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.sendTag("User_ID",auth.getCurrentUser().getEmail());
        //Buttons identifying and triggering;

        b1=(Button) findViewById(R.id.ok);
        b2=(Button) findViewById(R.id.non);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirection
                Intent intent1=new Intent(NotifyActivity.this,NaviguationActivity.class);
                startActivity(intent1);
            }
        });

    }

    public void update()
    {
        reference= FirebaseDatabase.getInstance().getReference().child("users"); //pointer sur la table users
        reference.child(auth.getCurrentUser().getUid()).child("infecte").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Vous venez d'annoncer que vous êtes malades. Tous vos amis seront notifiés ", Toast.LENGTH_SHORT).show();
                    //get our user name
                    reference.child(auth.getCurrentUser().getUid()).child("name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    username=snapshot.getValue(String.class);
                                    Log.d("usr", username);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                    //notify users
                    circlereference=FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid()).child("CircleMembers");
                    circlereference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    memberid = snap.child("circlememberid").getValue(String.class);
                                    // now we fetch the id
                                    reference.child(memberid)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    createUser = snapshot.getValue(CreateUser.class);
                                                    AsyncTask.execute(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            try {
                                                                String jsonResponse;

                                                                URL url = new URL("https://onesignal.com/api/v1/notifications");
                                                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                                                con.setUseCaches(false);
                                                                con.setDoOutput(true);
                                                                con.setDoInput(true);

                                                                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                                                con.setRequestProperty("Authorization", "Basic OWZjYmQwYjMtZDkyNi00NjRkLThiMDgtNjFkNjdmN2I4YmI3");
                                                                con.setRequestMethod("POST");

                                                                String sendemail = createUser.email;
                                                                String content= "Votre contacte "+username+" a attrapé le virus";
                                                                String strJsonBody = "{"
                                                                        + "\"app_id\": \"fe6affc0-47ff-41ed-b15b-5b3b9de5ca69\","
                                                                        + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + sendemail + "\"}],"
                                                                        + "\"data\": {\"foo\": \"bar\"},"
                                                                        + "\"contents\": {\"en\": \""+content+"\"}"
                                                                        + "}";


                                                                System.out.println("strJsonBody:\n" + strJsonBody);

                                                                byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                                                con.setFixedLengthStreamingMode(sendBytes.length);

                                                                OutputStream outputStream = con.getOutputStream();
                                                                outputStream.write(sendBytes);

                                                                int httpResponse = con.getResponseCode();
                                                                System.out.println("httpResponse: " + httpResponse);

                                                                if (httpResponse >= HttpURLConnection.HTTP_OK
                                                                        && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                                                    Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                                                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                                    scanner.close();
                                                                } else {
                                                                    Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                                                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                                                    scanner.close();
                                                                }
                                                                System.out.println("jsonResponse:\n" + jsonResponse);

                                                            } catch (Throwable t) {
                                                                t.printStackTrace();
                                                            }

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        });

                    //redirection
                    Intent intent1=new Intent(NotifyActivity.this,NaviguationActivity.class);
                    startActivity(intent1);
                }else{
                    Toast.makeText(getApplicationContext(), "Un problem est survenue ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

