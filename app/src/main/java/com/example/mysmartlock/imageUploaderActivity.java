package com.example.mysmartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class imageUploaderActivity extends AppCompatActivity {
    Button selectButton,uploadButton;
    Uri imageUri;
    ImageView uploadImageView;
    private static final int GALLERY_REQUEST_CODE=123;
    StorageReference mstorageRef;
    FirebaseAuth mAuth;
    DatabaseReference mPicRef;
    ProgressBar mprogressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_uploader);
        selectButton=(Button)findViewById(R.id.selectButton);
        uploadButton=(Button)findViewById(R.id.uploadButton);
        uploadImageView=(ImageView)findViewById(R.id.imageViewUploadActivity);
        mstorageRef=FirebaseStorage.getInstance().getReference();
        mPicRef= FirebaseDatabase.getInstance().getReference();
        mprogressBar=(ProgressBar)findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else
                isStoragePermissionGranted(imageUploaderActivity.this);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUploader();
                mprogressBar.setVisibility(View.VISIBLE);

            }
        });

    }
    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Pick an image"),1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            uploadImageView.setImageURI(imageUri);
            //Picasso.get().load(imageUri).centerInside().into(uploadImageView);
            Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_LONG).show();
        }else if(grantResults[0]==PackageManager.PERMISSION_DENIED){
            Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isStoragePermissionGranted(Activity activity){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }
            else{
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return false;
            }
        }else{
            return false;
        }
    }
    private void FileUploader(){
        String uid=mAuth.getUid();
        String path="Profile pic/"+uid+"/";
        final StorageReference imgname=mstorageRef.child(path);
        imgname.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgname.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put(mAuth.getCurrentUser().getUid(),String.valueOf(uri));
                        mPicRef.child("Storage links").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                                mprogressBar.setVisibility(View.GONE);
                                Intent intent=new Intent(getApplicationContext(),DashboardOwner.class);
                                startActivity(intent);
                            }
                        });

                    }
                });

            }
        });
    }
}