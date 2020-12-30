package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class lockActivity extends AppCompatActivity {
    Calendar calendar;
    SimpleDateFormat simpleDateFormat,simpleDateFormatDate;
    String Date;
TextView statusTxt,openedByTxt;
CardView cardView;
ArrayList<String> lockMemberList;
LinearLayout linearLayout;
Button buttonMember,toggleButton,enterButton,configureButton;
DatabaseReference lockRef,lockMember,updateLockInfoRef;
FirebaseAuth mAuth;
EditText MemberEditText,localIPEditText;
String nameOfUser="";
    String openedByUserInfo,time,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        statusTxt=(TextView)findViewById(R.id.lockActivityStatus);

        openedByTxt=(TextView)findViewById(R.id.lockActivityOpenedBy);
        linearLayout=(LinearLayout) findViewById(R.id.linearViewLockActivity);
        buttonMember=(Button)findViewById(R.id.lockActivityButton);
        toggleButton=(Button)findViewById(R.id.toggleButton);
        enterButton=(Button)findViewById(R.id.enterButton);
        configureButton=(Button)findViewById(R.id.configureButton);
        MemberEditText=(EditText)findViewById(R.id.memberNameEditText);
        localIPEditText=(EditText)findViewById(R.id.editTextLockActivity);
        lockRef= FirebaseDatabase.getInstance().getReference();
        lockMember=FirebaseDatabase.getInstance().getReference();
        updateLockInfoRef=FirebaseDatabase.getInstance().getReference();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mAuth=FirebaseAuth.getInstance();
        lockMemberList=new ArrayList<>();
        // getting the values from previous activity
        final String[] status = {getIntent().getExtras().getString("status")};
        String openedBy=getIntent().getExtras().getString("openedBy");
        int color=getIntent().getExtras().getInt("color");
        String LockNumber=getIntent().getExtras().getString("LockNumber");
        toggleButton.setText(status[0]);
        statusTxt.setText(status[0]);
        openedByTxt.setText(openedBy);
        // Fetching the data from firebase
        lockMember.child("Lock members").child(mAuth.getCurrentUser().getUid()).child(LockNumber).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data=dataSnapshot.getValue(String.class);
                Log.d("Username",data);
                lockMemberList.add(data);

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

        configureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),configurationActivity.class);
                intent.putExtra("localip",localIPEditText.getText().toString());
                startActivity(intent);

            }
        });



        enterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(lockMemberList.contains(MemberEditText.getText().toString())){
                    toggleButton.setVisibility(View.VISIBLE);
                    nameOfUser=MemberEditText.getText().toString();
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                }else{
                    Toast.makeText(getApplicationContext(),"Not allowed to use the lock",Toast.LENGTH_LONG).show();
                }
            }
        });
        // Fetching the time using Calender class




        // Setting the values in FIrebase
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar=Calendar.getInstance();

                simpleDateFormat=new SimpleDateFormat("hh:mm:ss a");

                simpleDateFormatDate=new SimpleDateFormat("EEEE, dd-MMM-yyyy ");

                time=simpleDateFormat.format(calendar.getTime());
                date=simpleDateFormatDate.format(calendar.getTime());
                if(status[0].equals("OFF"))
                    status[0] ="ON";
                else
                    status[0]="OFF";
                openedByUserInfo ="Power "+ status[0] +" by "+nameOfUser+" at "+time+" on "+date;
                Map<String,String> insertData=new HashMap<>();
                insertData.put("OpenedBy",openedByUserInfo);
                insertData.put("Status",status[0]);

                updateLockInfoRef.child("Locks").child(mAuth.getCurrentUser().getUid()).child(LockNumber).setValue(insertData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        toggleButton.setText(status[0]);
                    }
                });



            }
        });




    }


}