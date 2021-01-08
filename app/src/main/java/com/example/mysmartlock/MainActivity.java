package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.InternalTokenProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditText,usernameEditText;
    Button signButton;
    FirebaseAuth firebaseAuth;
    TextView signUp,singTextView;
    Boolean signUpactive=false;
    DatabaseReference ownerRef;
    public static final String SHARED_PREF="SharedPrefs";
    public static final String TEXT="Owner";
    Boolean isOwner;
    static Boolean check;
    static SharedPreferences sharedPreferences;
    ProgressBar mprogressbar;

    ArrayList<String> owner=new ArrayList<>();
    public void addDatabase(String e,String username,String id){

        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("USERS/");
        Map<String, Object> insertData = new HashMap<>();
        insertData.put("Username",username);
        insertData.put("Email ID", e);
        mUserRef.child(id).setValue(insertData);
    }

    public void showNextActivity(){
        Intent intent=new Intent(getApplicationContext(),DashboardNewUser.class);
        String email= firebaseAuth.getCurrentUser().getEmail();
        intent.putExtra("userEmailID",email );
        startActivity(intent);

    }
    public void signIn(View view){
        if(signUpactive==false) {

            if (!validateEmailAddress() | !validatePassword()) {
                return;
            }

            final String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            mprogressbar.setVisibility(View.VISIBLE);


            if(owner.contains(email)) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User Logged in", Toast.LENGTH_LONG).show();
                            check=true;
                            mprogressbar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), DashboardOwner.class);
                            startActivity(intent);
                        }
                        else {
                            mprogressbar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                passwordEditText.setError("Invalid Password");
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(MainActivity.this, "Email not in use", Toast.LENGTH_LONG).show();
                                emailEditText.setError("Email not in use");


                            }
                        }

                    }
                });
            }
            else{
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User Logged in", Toast.LENGTH_LONG).show();
                            check=false;
                            mprogressbar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), DashboardNewUser.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                passwordEditText.setError("Invalid Password");
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(MainActivity.this, "Email not in use", Toast.LENGTH_LONG).show();
                                emailEditText.setError("Email not in use");
                                mprogressbar.setVisibility(View.GONE);
                            }
                        }

                    }
                });


            }
        }else {
            if (!validateEmailAddress() | !validatePassword()) {
                return;
            }

            ;
            final String email = emailEditText.getText().toString().trim();

            String password = passwordEditText.getText().toString();
            final String username = usernameEditText.getText().toString();

            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User created succesfully ", Toast.LENGTH_LONG).show();
                            addDatabase(email,username, firebaseAuth.getCurrentUser().getUid());
                            check=false;
                            Intent in=new Intent(getApplicationContext(),DashboardNewUser.class);
                            startActivity(in);

                        } else {
                            Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(MainActivity.this, "Email already registered", Toast.LENGTH_LONG).show();
                                emailEditText.setError("Email already registered");
                            }

                        }

                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText=(EditText)findViewById(R.id.emailEditText);
        passwordEditText=(EditText)findViewById(R.id.passwordEditText);
        signUp=(TextView)findViewById(R.id.signUp);
        signButton =(Button)findViewById(R.id.signIn);
        singTextView=(TextView)findViewById(R.id.signTextView);
        usernameEditText=(EditText)findViewById(R.id.usernameEditText);
        mprogressbar=(ProgressBar)findViewById(R.id.progressBar);
        firebaseAuth =FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        isOwner = sharedPreferences.getBoolean(TEXT, false);
        if(firebaseAuth.getCurrentUser()!=null && isOwner){
            Intent intent = new Intent(getApplicationContext(), DashboardOwner.class);
            startActivity(intent);
        }else if(firebaseAuth.getCurrentUser()!=null && isOwner==false){
            Intent intent=new Intent(getApplicationContext(),DashboardNewUser.class);
            startActivity(intent);
        }

        ownerRef=FirebaseDatabase.getInstance().getReference("User Account");
        try {

            ownerRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String ownerKeyId = snapshot.getKey();
                    String ownerEmail = (String) snapshot.getValue();
                    Log.i("Owner", String.valueOf(ownerKeyId));
                    Log.i("Owner", ownerEmail);
                    owner.add(ownerEmail);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            // Log.d("Check", String.valueOf(owner.contains(emailEditText.getText().toString())));

        }catch (Exception e){
            e.printStackTrace();
        }










    }
    public boolean validateEmailAddress() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailEditText.setError("Email is empty, cannot be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid Email.Enter Valid email address");
            return false;
        }
        else {
            emailEditText.setError(null);
            return true;
        }
    }
    public boolean validatePassword(){
        String password=passwordEditText.getText().toString();
        if(password.isEmpty()){
            passwordEditText.setError("*Password cannot be empty, enter a valid password");
            return false;
        }
        else if(password.length()<6){
            passwordEditText.setError("Password is short.Minimum 6 characters required");
            return false;
        }
        else {
            passwordEditText.setError(null);
            return true;
        }
    }

    public void signUp(View view) {
        if(signUpactive==false){
            singTextView.setText("Sign Up");
            signUpactive=true;
            signUp.setText("Or,Sign In");
            usernameEditText.setVisibility(View.VISIBLE);
            signButton.setText("sign Up");
        }
        else{
            singTextView.setText("Sign In");
            signUpactive=false;
            signUp.setText("Or,Sign Up");
            usernameEditText.setVisibility(View.GONE);
            signButton.setText("sign In");
        }
    }



    @Override
    protected void onPause() {

        super.onPause();
        try {
            sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TEXT, check);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
