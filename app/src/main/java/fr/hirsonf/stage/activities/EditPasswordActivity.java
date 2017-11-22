package fr.hirsonf.stage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.hirsonf.stage.R;

public class EditPasswordActivity extends AppCompatActivity {

    @BindView(R.id.edit_password) EditText _passwordText;
    @BindView(R.id.edit_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.input_old_password) EditText _oldPasswordText;
    @BindView(R.id.b_ok) Button ok;
    @BindView(R.id.b_cancel) TextView cancel;

    private static final String TAG = "EditEmailActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        ButterKnife.bind(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    reauthenticate(firebaseUser);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditPasswordActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    reauthenticate(firebaseUser);
                }
            }
        });
    }

    public void reauthenticate(final FirebaseUser firebaseUser) {
        String oldPassword = _oldPasswordText.getText().toString();
        final String newPassword = _passwordText.getText().toString();

        AuthCredential credential = EmailAuthProvider
                .getCredential(firebaseUser.getEmail(), oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        firebaseUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updatePassword(firebaseUser, newPassword);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(TAG, "Error : password invalid ");
                            Toast.makeText(EditPasswordActivity.this,
                                    "Password invalid",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Error auth failed : ", e);
                        }
                    }
                });
    }

    public void updatePassword(FirebaseUser firebaseUser, String newPassword) {
        firebaseUser.updatePassword(newPassword)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Password updated");
                        Toast.makeText(EditPasswordActivity.this,
                                "Password updated",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPasswordActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "User password update error : ", e);
                        Toast.makeText(EditPasswordActivity.this,
                                "Email update error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String oldPassword = _oldPasswordText.getText().toString();

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

        if (oldPassword.isEmpty()) {
            _oldPasswordText.setError("Password is empty");
            valid = false;
        } else {
            _oldPasswordText.setError(null);
        }
        return valid;
    }
}
