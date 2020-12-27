package com.example.mysmartlock;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
    DatabaseReference mUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new_user);
        joinGroupButton=(Button)findViewById(R.id.joinGroupButton);
        newUserName=(TextView)findViewById(R.id.newUserName);
        newUserAccount=(TextView)findViewById(R.id.newUserAccount);
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

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
