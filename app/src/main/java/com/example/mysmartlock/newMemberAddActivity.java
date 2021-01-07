package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class newMemberAddActivity extends AppCompatActivity {
    DatabaseReference mNameRef,mcodeRef,mMemberRef,mMemberFetchRef;
    ArrayList<String> memberEmailList,keyList;
    ArrayList<String> memberNameList,memberAddedList;
    String code="",lockNumber;
    FirebaseAuth mAuth;
    int color;
    EditText codeEditText,nameEditText,emailEditText;
    ListView memberListView;
    ArrayAdapter arrayAdapter;
    Button submitCodeButton,addButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member_add);
        mNameRef=FirebaseDatabase.getInstance().getReference();
        mcodeRef=FirebaseDatabase.getInstance().getReference();


        codeEditText=(EditText)findViewById(R.id.codeEditText);
        emailEditText=(EditText)findViewById(R.id.memberEmailEditText) ;
        nameEditText=(EditText)findViewById(R.id.memberNameEditText);
        memberListView=(ListView)findViewById(R.id.memberNameListView);
        mAuth=FirebaseAuth.getInstance();
        memberAddedList=new ArrayList<>();
        memberEmailList =new ArrayList<>();
        memberNameList =new ArrayList<>();
        keyList=new ArrayList<>();

        mMemberFetchRef=FirebaseDatabase.getInstance().getReference();
        submitCodeButton=(Button)findViewById(R.id.submitCodeButton);
        addButton=(Button)findViewById(R.id.AddMemberButton);
        lockNumber=getIntent().getStringExtra("lock number");
        color=getIntent().getIntExtra("color",0);
        mMemberRef=FirebaseDatabase.getInstance().getReference().child("Lock members").child(mAuth.getUid()).child(lockNumber);

        // setting the color
        addButton.setBackgroundColor(color);
        submitCodeButton.setBackgroundColor(color);
        if(color== Color.YELLOW){
            addButton.setTextColor(Color.BLACK);
            submitCodeButton.setTextColor(Color.BLACK);
        }

       keyList.clear();
       memberEmailList.clear();
       memberNameList.clear();

        getEmailName();
        getcode();
        getMembers();
        arrayAdapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,memberAddedList);
        memberListView.setAdapter(arrayAdapter);
       submitCodeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (code.equals(codeEditText.getText().toString())) {
                   emailEditText.setVisibility(View.VISIBLE);
                   nameEditText.setVisibility(View.VISIBLE);
                   memberListView.setVisibility(View.VISIBLE);
                   addButton.setVisibility(View.VISIBLE);

               }
           }
       });
       addButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int pos1= memberEmailList.indexOf(emailEditText.getText().toString());
               int pos2= memberNameList.indexOf(nameEditText.getText().toString());
               if (memberEmailList.contains(emailEditText.getText().toString().trim()) && !memberAddedList.contains(emailEditText.getText().toString().trim()) &&
                       memberNameList.contains(nameEditText.getText().toString().trim()) && pos1==pos2) {
                   String key = mMemberRef.push().getKey();
                   mMemberRef.child(key).setValue(emailEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Toast.makeText(getApplicationContext(), "Added to list!", Toast.LENGTH_LONG).show();
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(getApplicationContext(), "Error occured!", Toast.LENGTH_LONG).show();
                       }
                   });

               } else if (!memberEmailList.contains(emailEditText.getText().toString().trim())) {
                   Toast.makeText(getApplicationContext(), "Not an authenticated user!", Toast.LENGTH_LONG).show();
               } else if (!memberNameList.contains(nameEditText.getText().toString().trim())) {
                   Toast.makeText(getApplicationContext(), "This name is not registered!", Toast.LENGTH_LONG).show();
               }else if(pos1!=pos2){
                   Toast.makeText(getApplicationContext(), "The name and email are not matching!", Toast.LENGTH_LONG).show();
               }
               else {
                   Toast.makeText(getApplicationContext(), "Already added!", Toast.LENGTH_LONG).show();
               }
           }

       });
       memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               new AlertDialog.Builder(newMemberAddActivity.this).setIcon(android.R.drawable.ic_delete).setTitle("Are you sure ?").setMessage("Do you want to remove the member")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               String key=keyList.get(position);
                               mMemberFetchRef.child("Lock members").child(mAuth.getCurrentUser().getUid()).child(lockNumber).child(key).removeValue()
                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Toast.makeText(getApplicationContext(),"Removed",Toast.LENGTH_LONG).show();
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(getApplicationContext(),"Removed",Toast.LENGTH_LONG).show();
                                   }
                               });
                               memberAddedList.remove(position);
                               arrayAdapter.notifyDataSetChanged();
                           }
                       }).setNegativeButton("No",null).show();
           }

       });









    }
    private void getMembers(){
        mMemberFetchRef.child("Lock members").child(mAuth.getCurrentUser().getUid()).child(lockNumber).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String data=dataSnapshot.getValue(String.class);
                Log.d("Username",data);
                memberAddedList.add(data);
                Log.d("Username",dataSnapshot.getKey());
                keyList.add(dataSnapshot.getKey());

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
    private void getEmailName(){
        mNameRef.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String,String> data=(Map<String, String>)dataSnapshot.getValue();
                Log.d("Username",data.get("Email ID"));
                memberEmailList.add(data.get("Email ID"));
                Log.d("Username",data.get("Username"));
                memberNameList.add(data.get("Username"));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String,String> data=(Map<String, String>)dataSnapshot.getValue();
                int p=0;
                int flag=0;
                for(int k=0;k<keyList.size();k++){
                    if(keyList.get(k).equals(dataSnapshot.getKey())==true){
                        p=k;
                        memberAddedList.set(p,data.get("Email ID"));
                        flag=1;

                    }else{
                        continue;
                    }
                }
                if(flag==0){
                    memberAddedList.add(data.get("Email ID"));
                }
                arrayAdapter.notifyDataSetChanged();

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
    private void getcode(){
        mcodeRef.child("Member add code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,Object> data=(Map<String,Object>)dataSnapshot.getValue();
                Log.d("Username",String.valueOf(data.get(mAuth.getUid())));
                code=String.valueOf(data.get(mAuth.getUid()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Username",databaseError.toString());

            }
        });
    }

}