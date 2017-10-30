package com.example.ronald.fetchme.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ronald.fetchme.MainActivity;
import com.example.ronald.fetchme.R;
import com.example.ronald.fetchme.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends BaseActivity {

    EditText email, password;
    Button sign_in;

    //Firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.editText_email);
        password = findViewById(R.id.editText_password);

        sign_in = findViewById(R.id.btn_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onSignInClicked(View v)
    {
        String userEmail, userPassword;

        userEmail = email.getText().toString().trim();
        userPassword = password.getText().toString().trim();

        showProgressDialog();

        if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
        {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(SignInActivity.this, "Signed in correctly", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent i = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(i);
                        //Send to Main menu

                        Utils.setPassword(SignInActivity.this, password.getText().toString());
                    }
                    else
                    {
                        Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            });
        }
        else
        {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        }

    }
}
