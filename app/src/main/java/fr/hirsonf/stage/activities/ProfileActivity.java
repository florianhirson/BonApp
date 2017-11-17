package fr.hirsonf.stage.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.hirsonf.stage.R;
import stage.bo.GlideApp;
import stage.bo.MyUser;

/**
 * Created by flohi on 10/11/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private SharedPreferences mPrefs;

    @BindView(R.id.profile) CircleImageView profile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        mPrefs = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        StorageReference profilePictureRef = storageRef.child("profilePictures")
                .child(firebaseUser.getUid())
                .child("profile.jpg");

        // Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)
        GlideApp.with(this /* context */)
                .load(profilePictureRef)
                .into(profile);


        MyUser myUser = new Gson().fromJson(mPrefs.getString("myUser", null), MyUser.class);
        Log.d(TAG, "MyUser : " + myUser);


    }

}
