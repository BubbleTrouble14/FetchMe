package com.example.ronald.fetchme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ronald.fetchme.authentication.AccountSettingsActivity;
import com.example.ronald.fetchme.authentication.AuthenticationActivity;
import com.example.ronald.fetchme.models.User;
import com.example.ronald.fetchme.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView txtEmail, txtUser;
    private ImageView profile_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        profile_picture = header.findViewById(R.id.imageView_nav_profile_picture);

        txtEmail = header.findViewById(R.id.navTxtEmail);
        txtUser = header.findViewById(R.id.navTxtUsername);

        mAuth = FirebaseAuth.getInstance();
        mDatabase =  FirebaseDatabase.getInstance().getReference(Constants.USER_KEY);

        if(mAuth.getCurrentUser() != null) {
            mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(MainActivity.this)
                                .load(user.photo_url)
                                .into(profile_picture);
                        txtEmail.setText(user.email);
                        txtUser.setText(user.username);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            startActivity(new Intent(this, AuthenticationActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_acc_settings) {
            Intent i = new Intent(this, AccountSettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_log_out)
        {
            if(mAuth.getCurrentUser() != null)
                {
                    mAuth.signOut();
                    finish();
                    Intent i = new Intent(MainActivity.this, AuthenticationActivity.class);
                    startActivity(i);
                    Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
