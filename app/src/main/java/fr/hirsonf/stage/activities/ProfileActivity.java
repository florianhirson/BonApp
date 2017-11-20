package fr.hirsonf.stage.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;

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
    private StorageReference profilePicturesRef;

    @BindView(R.id.profile) CircleImageView profile;
    @BindView(R.id.edit_name) EditText _nameText;
    @BindView(R.id.edit_address) EditText _addressText;
    @BindView(R.id.edit_mobile) EditText _mobileText;
    @BindView(R.id.edit_birthdate) EditText _birthdateText;

    @BindView(R.id.b_edit_email) Button editEmail;
    @BindView(R.id.b_edit_pwd) Button editPassword;
    @BindView(R.id.b_ok) Button ok;
    @BindView(R.id.b_cancel) TextView cancel;

    private static final String NAME_KEY = "name";
    private static final String ADDRESS_KEY = "address";
    private static final String MOBILE_KEY = "mobile";

    private ProgressDialog progress;
    private int RESULT_LOAD_IMG = 1;
    private int profilePictureVersion;
    private Uri selectedImage;
    private MyUser myUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Context context = getBaseContext();
        mPrefs = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
        profilePictureVersion = mPrefs.getInt("profilePictureVersion", 0);

        myUser = new Gson().fromJson(mPrefs.getString("myUser", null), MyUser.class);

        _nameText.setText(myUser.getName());
        _addressText.setText(myUser.getAddress());
        _mobileText.setText(myUser.getMobile());
        _birthdateText.setText(myUser.getBirthdate());

        fetchDataFromBundle(savedInstanceState);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(profilePictureVersion == 0) {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile.jpg");

        } else  {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile" + profilePictureVersion + ".jpg");
        }

        GlideApp.with(this /* context */)
                .load(profilePicturesRef)
                .into(profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(profilePictureVersion == 0) {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile.jpg");

        } else  {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile" + profilePictureVersion + ".jpg");
        }



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    myUser.setAddress(_addressText.getText().toString());
                    myUser.setMobile(_mobileText.getText().toString());
                    addData(firebaseUser);
                }
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,EditEmailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,EditPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/jpg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_KEY, _nameText.getText().toString());
        outState.putString(ADDRESS_KEY, _addressText.getText().toString());
        outState.putString(MOBILE_KEY, _mobileText.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addData(final FirebaseUser firebaseUser) {
        mDatabaseReference.child("users").child(firebaseUser.getUid()).setValue(myUser).
                addOnCompleteListener(ProfileActivity.this,
                        new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "profile editing:success");
                                Toast.makeText(ProfileActivity.this, "Profile editing : success",
                                        Toast.LENGTH_SHORT).show();
                                if(selectedImage != null) {
                                    createProfilePicture(firebaseUser);
                                }


                            }

                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "registration:failure");
                    }
                });
    }

    public void createProfilePicture(FirebaseUser firebaseUser) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        profilePictureVersion++;
        prefsEditor.putInt("profilePictureVersion", profilePictureVersion);
        prefsEditor.apply();

        if(profilePictureVersion == 0) {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile.jpg");

        } else  {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile" + profilePictureVersion + ".jpg");
        }

        profilePicturesRef.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Profile picture upload successful: ");
                        GlideApp.with(ProfileActivity.this)
                                .load(profilePicturesRef)
                                .centerCrop()
                                .into(profile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error in uploading : ", e.fillInStackTrace());
                        Toast.makeText(ProfileActivity.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void fetchDataFromBundle (Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(NAME_KEY)) {
                String name = savedInstanceState
                        .getString(NAME_KEY);
                _nameText.setText(name);
            }

            if (savedInstanceState.containsKey(ADDRESS_KEY)) {
                String address = savedInstanceState
                        .getString(ADDRESS_KEY);
                _addressText.setText(address);
            }

            if (savedInstanceState.containsKey(MOBILE_KEY)) {
                String mobile = savedInstanceState
                        .getString(MOBILE_KEY);
                _mobileText.setText(mobile);
            }

        }
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String mobile = _mobileText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (mobile.isEmpty()) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        return valid;
    }


}
