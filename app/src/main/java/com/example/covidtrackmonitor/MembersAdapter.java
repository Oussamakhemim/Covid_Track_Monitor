package com.example.covidtrackmonitor;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder>  {
    ArrayList<CreateUser> listname;
    Context context;
    MembersAdapter(ArrayList<CreateUser> listname, Context applicationContext) {
        this.listname=listname;
        this.context=applicationContext;

    }
    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.perso_layout,parent,false);
        MembersViewHolder membersViewHolder=new MembersViewHolder(v,context,listname);

        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {

        CreateUser currentuser=listname.get(position);
        holder.name.setText(currentuser.name);
        holder.lat.setText(currentuser.lat);
        holder.lng.setText(currentuser.lng);
        Picasso.get().load(currentuser.imageUrl).placeholder(R.drawable.profil).into(holder.circleImageView);
        ImageFilterView imageFilterView =holder.itemView.findViewById(R.id.state);
        if(currentuser.infecte.equals("true")){
            imageFilterView.setImageResource(R.drawable.ic_infecte);
        }
    }


    @Override
    public int getItemCount() {
        return listname.size();
    }



    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,lat,lng;
        CircleImageView circleImageView;
        Context context;
        ArrayList<CreateUser> listname;
        FirebaseAuth auth;
        FirebaseUser user;

        public MembersViewHolder(@NonNull View itemView, Context context, ArrayList<CreateUser> listname) {
            super(itemView);
            this.context = context;
            this.listname = listname;
            itemView.setOnClickListener(this);
            auth=FirebaseAuth.getInstance();
            user=auth.getCurrentUser();
            name=itemView.findViewById(R.id.titre);
            circleImageView=itemView.findViewById(R.id.circleImV);
            lat=itemView.findViewById(R.id.lat);
            lng=itemView.findViewById(R.id.lng);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context,"La dernière position de cet utilisateur est: latitude ="+this.lat.getText()+" longitude= "+this.lng.getText(),Toast.LENGTH_SHORT).show();
        }
    }
}
