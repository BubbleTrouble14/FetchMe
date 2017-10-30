package com.example.ronald.fetchme.authentication;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ronald on 10/28/2017.
 */

public class BaseActivity extends AppCompatActivity
{
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
}
