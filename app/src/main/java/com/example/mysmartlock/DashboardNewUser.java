package com.example.mysmartlock;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class DashboardNewUser extends AppCompatActivity {
    Button joinGroupButton,logOut;
    TextView newUserName,newUserAccount;
    FirebaseAuth mAuth;
    DatabaseReference mUsers,mpicRef;
    String url="";
    ImageView newUserProfileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new_user);
        joinGroupButton=(Button)findViewById(R.id.joinGroupButton);
        newUserName=(TextView)findViewById(R.id.newUserName);
        newUserAccount=(TextView)findViewById(R.id.newUserAccount);
        newUserProfileImageView=(ImageView)findViewById(R.id.newUserimage);
        mpicRef=FirebaseDatabase.getInstance().getReference();
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),joinNewGroup.class);
                startActivity(intent);
            }
        });
        logOut=(Button)findViewById(R.id.logOut);
        mAuth=FirebaseAuth.getInstance();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    mAuth.signOut();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        mUsers= FirebaseDatabase.getInstance().getReference();
        mUsers.child("USERS").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> data= (Map<String, String>)dataSnapshot.getValue();
                newUserName.setText(String.valueOf(data.get("Username")));
                newUserAccount.setText(data.get("Email ID"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadImage();
        newUserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),imageUploaderActivity.class);
                intent.putExtra("URL",url);
                intent.putExtra("Activity code","DashboardNewUser");
                startActivity(intent);
            }
        });

    }
    private void loadImage(){
        mpicRef.child("Storage links").child(mAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data=dataSnapshot.getValue(String.class);
                Log.d("Username",data);
                url=data;
                if(!data.equals(""))
                    Glide.with(DashboardNewUser.this).load(url).placeholder(R.drawable.profilepic).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(newUserProfileImageView);



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
