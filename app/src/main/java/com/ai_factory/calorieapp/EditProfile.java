package com.ai_factory.calorieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    EditText profileFullName,profileEmail;
    ImageView profileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    OutputStream outputStream;
    private static int REQUEST_CODE = 100;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Intent data = getIntent();
        final String fullName = data.getStringExtra("fullName");
        String email = data.getStringExtra("email");
//        fileUri=Uri.parse(data.getStringExtra("imageUri"));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileAge);
        profileImageView = findViewById(R.id.profileImageView);
        saveBtn = findViewById(R.id.saveProfileInfo);
//        if(fileUri!=null) profileImageView.setImageURI(fileUri);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(EditProfile.this, new String[] {Manifest.permission.CAMERA}, 1001);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(EditProfile.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1002);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(EditProfile.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1003);
                else {
                    ImagePicker
                            .Companion
                            .with(EditProfile.this)
                            .start();
                }

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileFullName.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = profileEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef=fStore.document("data/"+fAuth.getCurrentUser().getUid()+"/UserDetails/User");
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("mail",email);
                        edited.put("fname",profileFullName.getText().toString());
                        edited.put("ProfileImage",fileUri);
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        profileEmail.setText(email);
        profileFullName.setText(fullName);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if (data != null) {
                fileUri = data.getData();
                profileImageView.setImageURI(fileUri);
            }
        }
    }

    private void saveImage() {

        File dir = new File(Environment.getExternalStorageDirectory(),"SaveImage");

        if (!dir.exists()){

            dir.mkdir();

        }

        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File file = new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        Toast.makeText(getApplicationContext(),"Successfuly Saved",Toast.LENGTH_SHORT).show();

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveImg(Uri imageUri){
        int count=0;
        File sdDirectory = Environment.getExternalStorageDirectory();
        String FilePath = sdDirectory.getPath() + "/Calories";
        File subDirectory = new File(FilePath);
        File drawing = new File(subDirectory, "Drawing");
        Toast.makeText(getApplicationContext(),FilePath,Toast.LENGTH_LONG).show();

        if (!subDirectory.exists()) {
            subDirectory.mkdir();
        }
        if (subDirectory.exists()) {
            if (drawing.exists()) {
                File[] existing=drawing.listFiles();
                try{
                    for(File file:existing){
                        if(file.getName().endsWith(".jpg")||file.getName().endsWith(".png")){
                            count++;
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }else {
                drawing.mkdir();
            }
        }

        if (subDirectory.exists()) {
            if (drawing.exists()) {
                FileOutputStream fileOutputStream;
                File image_name=new File(drawing,"drawing"+(count+1)+".png");
                try{
                    fileOutputStream =new FileOutputStream(image_name);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Toast.makeText(getApplicationContext(),"Saved in Internal Storage",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
    }


//    public void saveImg(Uri imageUri){
//        File sdDirectory = Environment.getExternalStorageDirectory();
//        String FilePath = sdDirectory.toString() + "/Calorie Counter";
//        File subDirectory = new File(FilePath);
//        File image_file=new File(FilePath,"Profile.png");
//
//        if (!subDirectory.exists()) {
//            subDirectory.mkdir();
//        }
//
//        if (subDirectory.exists()) {
//                FileOutputStream fileOutputStream;
//                try{
//                    fileOutputStream =new FileOutputStream(image_file);
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    Toast.makeText(getApplicationContext(),FilePath+"Profile.png",Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//        }
//    }


}