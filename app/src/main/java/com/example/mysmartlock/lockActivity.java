package com.example.mysmartlock;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class lockActivity extends AppCompatActivity {
TextView statusTxt,openedByTxt;
CardView cardView;
LinearLayout linearLayout;
Button buttonMember,toggleButton;
DatabaseReference lockRef,lockMember;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        statusTxt=(TextView)findViewById(R.id.lockActivityStatus);

        openedByTxt=(TextView)findViewById(R.id.lockActivityOpenedBy);
        linearLayout=(LinearLayout) findViewById(R.id.linearViewLockActivity);
        buttonMember=(Button)findViewById(R.id.lockActivityButton);
        toggleButton=(Button)findViewById(R.id.toggleButton);
        lockRef= FirebaseDatabase.getInstance().getReference();
        lockMember=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        String status=getIntent().getExtras().getString("status");
        String openedBy=getIntent().getExtras().getString("openedBy");
        int color=getIntent().getExtras().getInt("color");
        String LockNumber=getIntent().getExtras().getString("LockNumber");
        statusTxt.setText(status);
        openedByTxt.setText(openedBy);
        lockMember.child("Lock members").child(mAuth.getCurrentUser().getUid()).child(LockNumber).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data=dataSnapshot.getValue(String.class);
                Log.d("Username",data);
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

        lockRef.child("Locks").child(mAuth.getUid()).child(LockNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> data=(Map<String,String>)dataSnapshot.getValue();
                Log.d("Username",data.get("Status"));
                Log.d("Username",data.get("OpenedBy"));
                statusTxt.setText(data.get("Status"));
                openedByTxt.setText(data.get("OpenedBy"));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if(color==Color.YELLOW){
            statusTxt.setTextColor(Color.BLACK);
            openedByTxt.setTextColor(Color.BLACK);
            buttonMember.setTextColor(Color.BLACK);
            toggleButton.setTextColor(Color.BLACK);
        }


        linearLayout.setBackgroundColor(color);
        buttonMember.setBackgroundColor(color);
        toggleButton.setBackgroundColor(color);


    }


}