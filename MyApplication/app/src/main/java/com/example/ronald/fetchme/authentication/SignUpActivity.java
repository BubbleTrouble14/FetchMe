package com.example.ronald.fetchme.authentication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ronald.fetchme.R;
import com.example.ronald.fetchme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText editText_email, editText_password, editText_name, editText_surname;
    Button sign_up;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editText_email = (EditText) findViewById(R.id.editText_reg_email);
        editText_name = (EditText) findViewById(R.id.editText_reg_name);
        editText_surname = (EditText) findViewById(R.id.editText_reg_surname);
        editText_password = (EditText) findViewById(R.id.editText_reg_password);
        sign_up = (Button) findViewById(R.id.btn_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void onSignUpClicked(View v)
    {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        String userEmail, userPassword;

        userEmail = editText_email.getText().toString();
        userPassword = editText_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    hideProgressDialog();
                    if(task.isSuccessful())
                    {
                        onAuthSuccess(task.getResult().getUser());
                        Toast.makeText(SignUpActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String username = editText_name.getText().toString() + " " + editText_surname.getText().toString();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();
                        user.updateProfile(profileUpdates);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = editText_name.getText().toString() + " " + editText_surname.getText().toString();

        writeNewUser(user.getUid(), username, user.getEmail());

        finish();
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editText_email.getText().toString())) {
            editText_email.setError("Required");
            result = false;
        } else {
            editText_email.setError(null);
        }

        if (TextUtils.isEmpty(editText_password.getText().toString())) {
            editText_password.setError("Required");
            result = false;
        } else {
            editText_password.setError(null);
        }

        if (TextUtils.isEmpty(editText_name.getText().toString())) {
            editText_name.setError("Required");
            result = false;
        } else {
            editText_name.setError(null);
        }
        if (TextUtils.isEmpty(editText_surname.getText().toString())) {
            editText_surname.setError("Required");
            result = false;
        } else {
            editText_surname.setError(null);
        }
        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

       // mDatabase.child("users").child(userId).setValue(user);
    }
}
