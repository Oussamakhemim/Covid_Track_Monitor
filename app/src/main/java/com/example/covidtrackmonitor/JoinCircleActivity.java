package com.example.covidtrackmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class JoinCircleActivity extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference reference, currentreference, circlereference;
    FirebaseUser user;
    FirebaseAuth auth;
    String current_user_id,join_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        pinview=(Pinview) findViewById(R.id.pinview);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        currentreference=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        current_user_id=user.getUid();

    }


    public void rejoindre(View v){
        //verify if all data is present in Database then
        //then find the user who has the code and create a node between then (CircleMembers) it means create a child in .
        Query query = reference.orderByChild("code").equalTo(pinview.getValue());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CreateUser createUser=null;
                    for (DataSnapshot child: snapshot.getChildren()){
                        createUser=child.getValue(CreateUser.class);
                        join_user_id=createUser.userid;


                        circlereference= FirebaseDatabase.getInstance().getReference().child("users")
                                .child(join_user_id).child("CircleMembers");
                        CircleJoin circleJoin= new CircleJoin(current_user_id);


                        circlereference.child(user.getUid()).setValue(circleJoin)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Utilisateur a rejoint le cercle avec succées" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Ce code est invalide " ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}