package com.example.ronald.fetchme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ronald.fetchme.R;
import com.example.ronald.fetchme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText cur_pass, new_pass, repeat_new_pass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        cur_pass = (EditText)findViewById(R.id.editText_change_password_current);
        new_pass = (EditText)findViewById(R.id.editText_change_password_new);
        repeat_new_pass = (EditText)findViewById(R.id.editText_change_password_repeat_new);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onChangePasswordClicked(View v)
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if (!validateForm()) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), cur_pass.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updatePassword(user);
                        }
                        else
                        {
                            Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user)
    {
        if(TextUtils.equals(new_pass.getText().toString(), repeat_new_pass.getText().toString()))
        {
            if(!(TextUtils.equals(cur_pass.getText().toString(), new_pass.getText().toString())))
            {
                user.updatePassword(new_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ChangePasswordActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Old Password is the same to the new Password ", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Password mismatch.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(cur_pass.getText().toString())) {
            cur_pass.setError("Required");
            result = false;
        } else {
            cur_pass.setError(null);
        }

        if (TextUtils.isEmpty(new_pass.getText().toString())) {
            new_pass.setError("Required");
            result = false;
        } else {
            new_pass.setError(null);
        }

        if (TextUtils.isEmpty(repeat_new_pass.getText().toString())) {
            repeat_new_pass.setError("Required");
            result = false;
        } else {
            repeat_new_pass.setError(null);
        }
        return result;
    }
}
