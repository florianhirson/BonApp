package fr.hirsonf.stage.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.hirsonf.stage.R;
import stage.bo.MyUser;

/**
 * Created by flohi on 07/11/2017.
 */

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindView(R.id.input_birthdate) EditText _birthdateText;
    @BindView(R.id.profile) CircleImageView profile;
    @BindView(R.id.input_first_name) EditText _firstnameText;
    @BindView(R.id.radiogroup_gender) RadioGroup radioGroupGender;
    @BindView(R.id.input_username) EditText _usernameText;

    private static final String NAME_KEY = "name";
    private static final String ADDRESS_KEY = "address";
    private static final String MAIL_KEY = "mail";
    private static final String MOBILE_KEY = "mobile";
    private static final String BIRTHDATE_KEY = "birthdate";
    private static final String PWD_KEY = "pwd";
    private static final String REPWD_KEY = "repwd";
    private static final String GENDER_KEY = "gender";
    private static final String FIRSTNAME_KEY = "firstname";
    private static final String USERNAME_KEY = "username";

    private ProgressDialog progress;

    private int RESULT_LOAD_IMG = 1;

    private Uri selectedImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private StorageReference profilePicturesRef;

    private Calendar now;
    private DatePickerDialog dpd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(NAME_KEY)) {
                String name = savedInstanceState
                        .getString(NAME_KEY);
                _nameText.setText(name);
            }

            if (savedInstanceState.containsKey(FIRSTNAME_KEY)) {
                String firstName = savedInstanceState
                        .getString(NAME_KEY);
                _firstnameText.setText(firstName);
            }

            if (savedInstanceState.containsKey(USERNAME_KEY)) {
                String username = savedInstanceState
                        .getString(USERNAME_KEY);
                _firstnameText.setText(username);
            }

            if (savedInstanceState.containsKey(GENDER_KEY)) {
                int genderId = savedInstanceState
                        .getInt(GENDER_KEY);
                radioGroupGender.check(genderId);
            }

            if (savedInstanceState.containsKey(MAIL_KEY)) {
                String mail = savedInstanceState
                        .getString(MAIL_KEY);
                _emailText.setText(mail);
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

            if (savedInstanceState.containsKey(BIRTHDATE_KEY)) {
                String birthdate = savedInstanceState
                        .getString(BIRTHDATE_KEY);
                _birthdateText.setText(birthdate);
            }

            if (savedInstanceState.containsKey(PWD_KEY)) {
                String pwd = savedInstanceState
                        .getString(PWD_KEY);
                _passwordText.setText(pwd);
            }

            if (savedInstanceState.containsKey(REPWD_KEY)) {
                String repwd = savedInstanceState
                        .getString(REPWD_KEY);
                _reEnterPasswordText.setText(repwd);
            }
        }

        now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                SignupActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor(getColor(R.color.primary));
    }

    @Override
    protected void onStart() {
        super.onStart();
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate())
                    signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });

        _birthdateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_KEY, _nameText.getText().toString());
        outState.putString(MAIL_KEY, _emailText.getText().toString());
        outState.putString(ADDRESS_KEY, _addressText.getText().toString());
        outState.putString(MOBILE_KEY, _mobileText.getText().toString());
        outState.putString(BIRTHDATE_KEY, _birthdateText.getText().toString());
        outState.putString(PWD_KEY, _passwordText.getText().toString());
        outState.putString(REPWD_KEY, _reEnterPasswordText.getText().toString());
        outState.putString(FIRSTNAME_KEY, _firstnameText.getText().toString());
        outState.putString(USERNAME_KEY, _usernameText.getText().toString());
        outState.putInt(GENDER_KEY, radioGroupGender.getCheckedRadioButtonId());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        _birthdateText.setText(date);
    }
    public void signup() {
        progress = new ProgressDialog(this);
        progress.setMessage("Registering profile data");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        Log.d(TAG, "Signup");
        int radioButtonID = radioGroupGender.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroupGender.findViewById(radioButtonID);
        _signupButton.setEnabled(false);

        final String name = _nameText.getText().toString();
        final String address = _addressText.getText().toString();
        final String email = _emailText.getText().toString();
        final String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String birthdate = _birthdateText.getText().toString();
        final String firstname = _firstnameText.getText().toString();
        final String gender = radioButton.getText().toString();
        final String username = _usernameText.getText().toString();

        final MyUser user = new MyUser();
        user.setName(name);
        user.setEmail(email);
        user.setAddress(address);
        user.setBirthdate(birthdate);
        user.setMobile(mobile);
        user.setUserName(username);
        user.setFirstName(firstname);
        user.setGender(gender);


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            addData(firebaseUser, user);
                            createProfilePicture(firebaseUser);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        _signupButton.setEnabled(true);
    }

    public void addData(FirebaseUser firebaseUser, MyUser user) {
        mDatabaseReference.child("users").child(firebaseUser.getUid()).setValue(user).
                addOnCompleteListener(SignupActivity.this,
                        new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "registration:success");
                                Toast.makeText(SignupActivity.this, "Check your emails to verify your account.",
                                        Toast.LENGTH_SHORT).show();
                                mSendEmailVerification();
                            }

                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "registration:failure");
                    }
                });
    }

    public void createProfilePicture(final FirebaseUser firebaseUser) {
        profilePicturesRef = storageRef.child("profilePictures").child(firebaseUser.getUid()).child("profile");
        profilePicturesRef.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(SignupActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
                addPictureReferenceToDatabase(firebaseUser);
                progress.dismiss();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error in uploading : ", e.fillInStackTrace());
                Toast.makeText(SignupActivity.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addPictureReferenceToDatabase(FirebaseUser firebaseUser) {
        DatabaseReference pictureRef = mDatabaseReference.child("users").child(firebaseUser.getUid()).child("picture");
        pictureRef.setValue("profile")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "adding picture reference into database:success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding picture reference into database : ", e.fillInStackTrace());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void mSendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("fr.hirsonf.fr",true, null)
                .setUrl("https://projet-de-stage.firebaseapp.com/__/auth/action?mode=%3Caction%3E&oobCode=%3Ccode%3E")
                .build();
        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignupActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String birthdate = _birthdateText.getText().toString();
        String firstname = _usernameText.getText().toString();
        String username = _usernameText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("At least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (firstname.isEmpty() || firstname.length() < 3) {
            _firstnameText.setError("At least 3 characters");
            valid = false;
        } else {
            _firstnameText.setError(null);
        }

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("At least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if(birthdate.isEmpty()) {
            _birthdateText.setError("Choose a date");
        } else {
            _birthdateText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty()) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("Minimum 6 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if(selectedImage == null) {
            valid = false;
            Toast.makeText(SignupActivity.this,"Please choose a profile picture !",Toast.LENGTH_SHORT).show();
        }

        return valid;
    }
}