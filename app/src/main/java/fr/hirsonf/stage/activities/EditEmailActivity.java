package fr.hirsonf.stage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.hirsonf.stage.R;
import stage.bo.MyUser;

public class EditEmailActivity extends AppCompatActivity {

    @BindView(R.id.edit_email) EditText editMail;
    @BindView(R.id.re_enter_edit_email) EditText reEnterEditMail;
    @BindView(R.id.actual_mail) EditText actualMail;
    @BindView(R.id.b_ok) Button ok;
    @BindView(R.id.b_cancel) TextView cancel;

    private static final String TAG = "EditEmailActivity";
    private String currentEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
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
        currentEmail = firebaseUser.getEmail();
        actualMail.setText("Current Email : " + currentEmail);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    updateEmail(firebaseUser);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEmailActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditEmailActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void updateEmail(final FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(editMail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User email address updated.");
                        updateEmailInDatabase(firebaseUser);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "User email address update error : ", e);
                        Toast.makeText(EditEmailActivity.this,
                                "Email update error",
                                Toast.LENGTH_SHORT).show();
                    }
        });

    }

    public  void updateEmailInDatabase(final FirebaseUser firebaseUser) {
        mDatabaseReference.child("users").child(firebaseUser.getUid()).child("email").setValue(editMail.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User email address updated in realtime database.");
                        Toast.makeText(EditEmailActivity.this,
                                "Email changed",
                                Toast.LENGTH_SHORT).show();
                        firebaseUser.sendEmailVerification();
                        Intent intent = new Intent(EditEmailActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "User email address update error : ", e);
                        Toast.makeText(EditEmailActivity.this,
                                "Email update error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String email = editMail.getText().toString();
        String reEmail = reEnterEditMail.getText().toString();

        if(email.isEmpty()) {
            editMail.setError("Email address can't be empty");
            valid = false ;
        } else {
            editMail.setError("");
        }

        if(reEmail.isEmpty()) {
            reEnterEditMail.setError("Email address can't be empty");
            valid = false ;
        } else {
            reEnterEditMail.setError("");
        }

        if(!email.equals(reEmail)) {
            editMail.setError("Email address do not match");
            reEnterEditMail.setError("Email address do not match");
            valid = false ;
        } else {
            editMail.setError("");
            reEnterEditMail.setError("");
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editMail.setError("Not a valid email address");
            valid = false ;
        } else {
            editMail.setError("");
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(reEmail).matches()) {
            reEnterEditMail.setError("Email address can't be empty");
            valid = false ;
        } else {
            reEnterEditMail.setError("");
        }
        return valid;
    }
}
