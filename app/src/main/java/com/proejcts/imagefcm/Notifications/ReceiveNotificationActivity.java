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

//        TextView categotyTv = findViewById(R.id.category);
//        TextView brandTv = findViewById(R.id.brand);

//        if (getIntent().hasExtra("category")) {
//            String category = getIntent().getStringExtra("category");
//            String brand = getIntent().getStringExtra("brandId");
//            categotyTv.setText(category);
//            brandTv.setText(brand);
//        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Using "Picasso" (http://square.github.io/picasso/) after adding the dependency in the Gradle.
                // ".fit().centerInside()" fits the entire image into the specified area.
                // Finally, add "READ" and "WRITE" external storage permissions in the Manifest.
                Picasso.get().load(uri).fit().centerInside().into(iv_load);
                mPgBar2.setVisibility(View.GONE);
                Toast.makeText(ReceiveNotificationActivity.this, "Loading Image.....", Toast.LENGTH_SHORT).show();
//                Picasso.get().load(uri).fit().transform(new BlurTransformation(getContext(), 25, 1)).into(profileBackgroundImage);


            }
        });
    }
}
