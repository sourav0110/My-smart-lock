package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class joinNewGroup extends AppCompatActivity {
    Button submit;
    EditText ownerEmailId, ownerPassword;
    FirebaseAuth myAuth;
    public static final String SHARED_PREF="SharedPrefs";
    public static final String TEXT="Owner";
    ProgressBar mprogressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_new_group);
        submit=(Button)(findViewById(R.id.submit));
        ownerEmailId=(EditText)findViewById(R.id.OwnerEmailEditText);
        ownerPassword=(EditText)findViewById(R.id.OwnerPassword);
        myAuth=FirebaseAuth.getInstance();
        mprogressBar=(ProgressBar)findViewById(R.id.progressBarJoinGroup);
        Toast.makeText(getApplicationContext(),"Enter the credentials ",Toast.LENGTH_LONG).show();
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!validateEmailAddress() | !validatePassword()) {
                    return;
                }
                mprogressBar.setVisibility(View.VISIBLE);
                String email = ownerEmailId.getText().toString().trim();
                String password = ownerPassword.getText().toString();


                myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Logged in", Toast.LENGTH_LONG).show();
                            MainActivity.check=true;
                            mprogressBar.setVisibility(View.GONE);

                          Intent in =new Intent(getApplicationContext(),DashboardOwner.class);
                           startActivity(in);

                        } else {
                            Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                                ownerPassword.setError("Invalid Password");
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(getApplicationContext(), "Email not in use", Toast.LENGTH_LONG).show();
                                ownerEmailId.setError("Email not in use");
                            }
                            mprogressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });
    }
    public boolean validateEmailAddress() {
        String email = ownerEmailId.getText().toString().trim();
        if (email.isEmpty()) {
            ownerEmailId.setError("Email is empty, cannot be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ownerEmailId.setError("Invalid Email.Enter Valid email address");
            return false;
        }
        else {
            ownerEmailId.setError(null);
            return true;
        }
    }
    public boolean validatePassword(){
        String password=ownerPassword.getText().toString();
        if(password.isEmpty()){
            ownerPassword.setError("*Password cannot be empty, enter a valid password");
            return false;
        }
        else if(password.length()<6){
            ownerPassword.setError("Password is short.Minimum 6 characters required");
            return false;
        }
        else {
            ownerPassword.setError(null);
            return true;
        }
    }
    @Override
    protected void onPause() {

        super.onPause();
        MainActivity.sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        editor.putBoolean(TEXT, MainActivity.check);
        editor.commit();
    }





}


