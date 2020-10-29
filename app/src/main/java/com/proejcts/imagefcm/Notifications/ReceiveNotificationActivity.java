package com.proejcts.imagefcm.Notifications;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.proejcts.imagefcm.R;
import com.squareup.picasso.Picasso;

public class ReceiveNotificationActivity extends AppCompatActivity {
    ImageView iv_load;
    ProgressBar mPgBar2;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve_notification);
        iv_load = findViewById(R.id.iv_load);
        mPgBar2 = findViewById(R.id.mPgBar2);
        firebaseAuth = FirebaseAuth.getInstance();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(iv_load);
                mPgBar2.setVisibility(View.GONE);
                Toast.makeText(ReceiveNotificationActivity.this, "Loading Image.....", Toast.LENGTH_SHORT).show();


            }
        });
    }
}
