package com.proejcts.imagefcm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.proejcts.imagefcm.Notifications.ReceiveNotificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;
    private final String URL = "https://fcm.googleapis.com/fcm/send";
    ImageView iv_upload;
    Button btn_choose, btn_upload, btn_signOut;
    Bitmap bitmap;
    FirebaseAuth firebaseAuth;
    ProgressBar mPgBar;
    private Uri imagePath;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        iv_upload = findViewById(R.id.iv_upload);
        btn_choose = findViewById(R.id.btn_choose);
        btn_upload = findViewById(R.id.btn_upload);
        btn_signOut = findViewById(R.id.btn_signOut);
        mPgBar = findViewById(R.id.mPgBar);
        firebaseAuth = FirebaseAuth.getInstance();
        btn_upload.setOnClickListener(v -> {
            mPgBar.setVisibility(View.VISIBLE);
            sendUserData();
        });

        btn_choose.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permission, 1001);
                } else {
                    //Permission already granted
                    getImage();
                }
            } else {
                getImage();
            }
        });
        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(AdminPanel.this, MainActivity.class));

            }
        });

        if (getIntent().hasExtra("category")) {
            Intent intent = new Intent(AdminPanel.this, ReceiveNotificationActivity.class);
            intent.putExtra("category", getIntent().getStringExtra("category"));
            intent.putExtra("brandId", getIntent().getStringExtra("brandId"));
            startActivity(intent);
        }
        mRequestQue = Volley.newRequestQueue(this);
        FirebaseMessaging.getInstance().subscribeToTopic("demo");


    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission was granted
                getImage();
            } else {
                //Permission was denied
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        assert data != null;
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data.getData() != null) {
            imagePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                iv_upload.setImageBitmap(bitmap);

            } catch (IOException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendUserData() {
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("images"); //User id/Images/Profile Pic.jpg
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: Uploading profile picture", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(taskSnapshot -> {
            sendNotification();
            mPgBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Profile picture uploaded", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "demo");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Admin uploaded an image.");
            notificationObj.put("body", "click to check it out!");


            json.put("notification", notificationObj);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("MUR", "onResponse: "), error -> Log.d("MUR", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAn8BjkAA:APA91bHlWw4gViSTm5YBKdONavk0K43q7CTye9SVM5o8SmSfYOegtIIvCW2OZsYiwp6dkPGAKMrqLlGEumcV__jk6PkfUAxTT827YV-6R3JwbUGgwJ_tGWQ0ELEUarLR902SYay1Dg7T");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}