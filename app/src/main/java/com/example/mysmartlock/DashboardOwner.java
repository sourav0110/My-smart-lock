package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class DashboardOwner extends AppCompatActivity {
    Button joinGroupButton,logOut;
    FirebaseAuth mAuth;
    ListView lockList;
    int [] images={R.drawable.lock1,R.drawable.lock2,R.drawable.lock3,R.drawable.lock4};
    int[] color={Color.YELLOW,Color.RED,Color.GREEN,Color.BLUE};
    ArrayList<String> lockStatus =new ArrayList<>();
    ArrayList<String> openedBy =new ArrayList<>();
    ArrayList<String> keyList=new ArrayList<>();
    DatabaseReference namesRef,AccRef,UserLockRef,mpicRef;
    String usernameFB,UserAccFB,url="";
    TextView OwnerName,OwnerAccount;
    ImageView profileImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_owner);

        logOut=(Button)findViewById(R.id.logOutOwner);
        mAuth= FirebaseAuth.getInstance();
        lockList=(ListView)findViewById(R.id.locklist);
        profileImage=(ImageView)findViewById(R.id.ownerimage);
        String UID=mAuth.getUid();
        OwnerAccount=(TextView)findViewById(R.id.OwnerAccount);
        OwnerName=(TextView)findViewById(R.id.OwnerName);
      namesRef =FirebaseDatabase.getInstance().getReference();
      mpicRef=FirebaseDatabase.getInstance().getReference();
      try {
          namesRef.child("Username").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                  // Log.d("Username",data.get(mAuth.getUid()));
                  usernameFB = data.get(mAuth.getUid());
                  OwnerName.setText(data.get(mAuth.getUid()));

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
          AccRef = FirebaseDatabase.getInstance().getReference();
          AccRef.child("User Account").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                  Log.d("Username", data.get(mAuth.getUid()));
                  UserAccFB = data.get(mAuth.getUid());
                  OwnerAccount.setText(data.get(mAuth.getUid()));
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
      }catch (Exception e){
          e.printStackTrace();
      }
      UserLockRef=FirebaseDatabase.getInstance().getReference();
        CustomAdapter customAdapter=new CustomAdapter();
        lockList.setAdapter(customAdapter);
        keyList.clear();
        lockStatus.clear();
        openedBy.clear();
        try {
            UserLockRef.child("Locks").child(UID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                    Log.d("Username", data.get("Status"));
                    lockStatus.add(data.get("Status"));

                    Log.d("Username", data.get("OpenedBy"));
                    openedBy.add(data.get("OpenedBy"));
                    keyList.add(String.valueOf(dataSnapshot.getKey()));
                    Log.d("Username", String.valueOf(dataSnapshot.getKey()));
                    customAdapter.notifyDataSetChanged();


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Log.d("Username", dataSnapshot.getKey());
                    Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();

                    int p = 0;
                    int flag = 0;
                    for (int i = 0; i < keyList.size(); i++) {
                        if (keyList.get(i).equals(dataSnapshot.getKey()) == true) {
                            p = i;
                            lockStatus.set(p, data.get("Status"));
                            openedBy.set(p, data.get("OpenedBy"));
                            flag = 1;

                        } else {
                            continue;
                        }


                    }
                    if (flag == 0) {
                        lockStatus.add(data.get("Status"));
                        openedBy.add(data.get("OpenedBy"));
                    }
                    customAdapter.notifyDataSetChanged();


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

            mpicRef.child("Storage links").child(mAuth.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String data = dataSnapshot.getValue(String.class);
                    Log.d("Username", data);
                    url = data;
                    if (!data.equals(""))
                        Glide.with(DashboardOwner.this).load(url).placeholder(R.drawable.profilepic).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImage);


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
            Log.d("Username", String.valueOf(lockStatus.size()));
        }catch (Exception e){
            e.printStackTrace();
        }
        try {

            lockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Intent intent = new Intent(getApplicationContext(), lockActivity.class);
                                                    intent.putExtra("status", lockStatus.get(position));
                                                    intent.putExtra("openedBy", openedBy.get(position));
                                                    intent.putExtra("LockNumber", keyList.get(position));
                                                    intent.putExtra("color", color[position]);
                                                    startActivity(intent);
                                                }
                                            }
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }







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
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),imageUploaderActivity.class);
                intent.putExtra("URL",url);
                intent.putExtra("Activity code","DashboardOwner");
                startActivity(intent);

            }
        });



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lockStatus.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=getLayoutInflater().inflate(R.layout.customlayout,null);
            ImageView mImages=view.findViewById(R.id.UserImage);
            TextView mopenedBy=view.findViewById(R.id.OpenedBy);
            TextView mstatus=view.findViewById(R.id.status);
            mImages.setImageResource(images[position]);
            mstatus.setText(lockStatus.get(position));
            mopenedBy.setText(openedBy.get(position));
            return view;
        }
    }
}

