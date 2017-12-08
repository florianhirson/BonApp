package fr.hirsonf.stage.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import stage.utils.GlideApp;
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
    @BindView(R.id.edit_first_name) EditText _firstnameText;
    @BindView(R.id.edit_gender) EditText _genderText;
    @BindView(R.id.edit_user_name) EditText _usernameText;

    @BindView(R.id.b_edit_email) Button editEmail;
    @BindView(R.id.b_edit_pwd) Button editPassword;
    @BindView(R.id.b_ok) Button ok;
    @BindView(R.id.b_cancel) TextView cancel;
    @BindView(R.id.my_toolbar) Toolbar myToolbar;

    private static final String NAME_KEY = "name";
    private static final String ADDRESS_KEY = "address";
    private static final String MOBILE_KEY = "mobile";

    private int RESULT_LOAD_IMG = 1;
    private int profilePictureVersion;
    private Uri selectedImage;
    private MyUser myUser;
    private String pictureName;
    private ProgressDialog progress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Context context = getBaseContext();
        mPrefs = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
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
        _genderText.setText(myUser.getGender());
        _usernameText.setText(myUser.getUserName());
        _firstnameText.setText(myUser.getFirstName());

        fetchDataFromBundle(savedInstanceState);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        getPictureRef(firebaseUser);


    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(profilePictureVersion == 0) {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile");

        } else  {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile" + profilePictureVersion);
        }



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    progress = new ProgressDialog(ProfileActivity.this);
                    progress.setMessage("Uploading data");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                    myUser.setAddress(_addressText.getText().toString());
                    myUser.setMobile(_mobileText.getText().toString());
                    myUser.setUserName(_usernameText.getText().toString());
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
    protected void onPause() {
        super.onPause();
        if(progress != null)
            progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progress != null)
            progress.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                return true;

            case R.id.action_delete_account:
                deleteAccountVerification();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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



    public void deleteAccountVerification() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final EditText input = new EditText(this);
        input.setHint("Password");


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Type your password to delete your account")
                .setTitle("Delete your account")
                .setView(input);


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProfilePicture(firebaseUser, input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteProfileData(final FirebaseUser firebaseUser, final String pwd) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(firebaseUser.getUid()).removeValue()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Deleting profile data : success");
                reauthenticate(firebaseUser, pwd);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Deleting profile data : failure", e);
            }
        });
    }

    public void reauthenticate(final FirebaseUser firebaseUser, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseUser.getEmail(), password);

        firebaseUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteAccount(firebaseUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void deleteProfilePicture(final FirebaseUser firebaseUser, final String pwd) {
        Log.d(TAG,"picture reference : " + profilePicturesRef);
        profilePicturesRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Deleting profile picture : success");
                        deleteProfileData(firebaseUser, pwd);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Deleting profile picture : failure", e);
                        Log.d(TAG, "Cause : " + e.getCause());
                    }
                });
    }

    public void deleteAccount(final FirebaseUser firebaseUser) {
        firebaseUser.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Deleting account : success");
                        Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Deleting account : failure", e);
                    }
                });
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
                                } else {
                                    progress.dismiss();
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

    public void updatePictureRef(final FirebaseUser firebaseUser, String name) {
        DatabaseReference pictureRef = mDatabaseReference.child("users").child(firebaseUser.getUid()).child("picture");
        pictureRef.setValue(name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "adding picture reference into database:success");
                        getPictureRef(firebaseUser);
                        progress.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding picture reference into database : ", e.fillInStackTrace());
                    }
                });
    }

    public void createProfilePicture(final FirebaseUser firebaseUser) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        profilePictureVersion++;
        prefsEditor.putInt("profilePictureVersion", profilePictureVersion);
        prefsEditor.apply();
        pictureName = "profile" + profilePictureVersion;

        if(profilePictureVersion == 0) {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child("profile");

        } else  {
            profilePicturesRef = storageRef.child("profilePictures")
                    .child(firebaseUser.getUid()).child(pictureName);
        }

        profilePicturesRef.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Profile picture upload successful: ");
                        updatePictureRef(firebaseUser, pictureName);


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

    public void getPictureRef(final FirebaseUser firebaseUser) {
        DatabaseReference ref = mDatabaseReference.child("users").child(firebaseUser.getUid()).child("picture");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Picture reference download successful: ");
                pictureName = dataSnapshot.getValue().toString();
                profilePicturesRef = storageRef.child("profilePictures")
                        .child(firebaseUser.getUid()).child(pictureName);
                GlideApp.with(ProfileActivity.this)
                        .load(profilePicturesRef)
                        .centerCrop()
                        .into(profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Picture reference download failed: ", databaseError.toException());
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
