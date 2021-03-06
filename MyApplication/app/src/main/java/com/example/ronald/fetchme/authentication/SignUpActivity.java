package com.example.ronald.fetchme.authentication;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ronald.fetchme.R;
import com.example.ronald.fetchme.models.User;
import com.example.ronald.fetchme.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends BaseActivity {

    EditText editText_email, editText_password, editText_name, editText_surname;
    Button sign_up;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editText_email =  findViewById(R.id.editText_reg_email);
        editText_name = findViewById(R.id.editText_reg_name);
        editText_surname =  findViewById(R.id.editText_reg_surname);
        editText_password =  findViewById(R.id.editText_reg_password);
        sign_up = findViewById(R.id.btn_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

        writeNewUser(username, user.getEmail(), user.getUid());

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

    private void writeNewUser(String name, String email, String uid) {
        User user = new User(name, email, "", uid);

        mDatabase.child(Constants.USER_KEY).child(uid).setValue(user);
    }
}
