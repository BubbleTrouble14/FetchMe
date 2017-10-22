package com.example.ronald.fetchme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText editText_name, editText_surname, editText_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        editText_name = (EditText)findViewById(R.id.editText_acc_name);
        editText_surname = (EditText)findViewById(R.id.editText_acc_surname);
        editText_email = (EditText)findViewById(R.id.editText_acc_email);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null)
        {
            editText_email.setText(user.getEmail());
            String name = user.getDisplayName();
            String[] info = name.split("\\s+");
            editText_name.setText(info[0]);
            editText_surname.setText(info[1]);
        }
        else
        {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            Intent i = new Intent(this, AuthenticationActivity.class);
            startActivity(i);
        }
    }

    public void onSettingsChangePasswordClicked(View v)
    {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }
}
