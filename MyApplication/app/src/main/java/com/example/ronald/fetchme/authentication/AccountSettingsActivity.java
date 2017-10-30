package com.example.ronald.fetchme.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ronald.fetchme.R;
import com.example.ronald.fetchme.models.User;
import com.example.ronald.fetchme.utils.Constants;
import com.example.ronald.fetchme.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountSettingsActivity extends BaseActivity implements FirebaseAuth.AuthStateListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView picture;
    private EditText editText_name, editText_surname, editText_email;
    private String[] info;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private static final int GALLERY_INTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        editText_name = findViewById(R.id.editText_acc_name);
        editText_surname = findViewById(R.id.editText_acc_surname);
        editText_email = findViewById(R.id.editText_acc_email);
        picture = findViewById(R.id.img_profle_picture);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getIntent().setType("image/*");
                startActivityForResult(i, GALLERY_INTENT);

                Toast.makeText(AccountSettingsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.USER_KEY);
        mAuthListener = this;

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null) {
                    //Prevent Crashing when it thinks the activity is destroyed
                    if(!AccountSettingsActivity.this.isFinishing()) {
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(AccountSettingsActivity.this)
                                .load(user.photo_url)
                                .into(picture);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT)
        {
            if(resultCode == RESULT_OK) {
                final Uri uri = data.getData();
                final StorageReference filePath = mStorage.child(Constants.USER_KEY).child(mAuth.getUid()).child("profile_image");

                showProgressDialog();

                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                FirebaseUser user = mAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                    User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), String.valueOf(firebaseUser.getPhotoUrl()), firebaseUser.getUid());
                                                    mDatabase.child(mAuth.getUid()).setValue(user);
                                                    Toast.makeText(AccountSettingsActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                hideProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AccountSettingsActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        });


                    }
                });
            }
        }
    }

    public void onDeleteAccountClicked(View view)
    {
        FirebaseUser user = mAuth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), Utils.getPassword(this));

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful()) {
                            openDialogBox();
                        }
                        else
                        {
                            Toast.makeText(AccountSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openDialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to delete this account?")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AccountSettingsActivity.this, "User account deleted", Toast.LENGTH_SHORT).show();
                                mDatabase.child(mAuth.getUid()).removeValue();
                                finish();
                                startActivity(new Intent(AccountSettingsActivity.this, AuthenticationActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AccountSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              //  finish();
            }
        });
        builder.show();
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
            info = name.split("\\s+");
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

    public void onSaveAccountClicked(View v)
    {
        showProgressDialog();

        if (!validateForm()) {
            return;
        }

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), Utils.getPassword(this));

        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful()) {
                            updateUser();
                        }
                        else
                        {
                            Toast.makeText(AccountSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    public void updateUser()
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String name = editText_name.getText().toString() + " " + editText_surname.getText().toString();
        User user = new User(name, editText_email.getText().toString(), String.valueOf(firebaseUser.getPhotoUrl()), firebaseUser.getUid());

        if(!TextUtils.equals(editText_name.getText().toString(), info[0]) || !TextUtils.equals(editText_surname.getText().toString(), info[1]))
        {
            mDatabase.child(mAuth.getUid()).setValue(user);
            firebaseUser.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(editText_name.getText().toString() + " " + editText_surname.getText().toString())
                    .build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(AccountSettingsActivity.this, "Successfully changed Data", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                            else
                            {
                                Toast.makeText(AccountSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        }
                    });
        }

        if(!TextUtils.equals(editText_email.getText().toString(), firebaseUser.getEmail())) {
            mDatabase.child(mAuth.getUid()).setValue(user);
            firebaseUser.updateEmail(editText_email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountSettingsActivity.this, "Successfully changed Data", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                            else
                            {
                                Toast.makeText(AccountSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editText_email.getText().toString())) {
            editText_email.setError("Required");
            result = false;
        } else {
            editText_email.setError(null);
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
}
