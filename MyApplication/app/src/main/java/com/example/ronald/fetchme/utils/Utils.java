package com.example.ronald.fetchme.utils;

import android.content.Context;

import com.securepreferences.SecurePreferences;

/**
 * Created by ronald on 10/30/2017.
 */


public class Utils
{
    public static void setPassword(Context ctx, String pass)
    {
        SecurePreferences securePrefs = new SecurePreferences(ctx);
        SecurePreferences.Editor editor = securePrefs.edit();
        editor.putString(Constants.PASSWORD, pass);
        editor.commit();
    }

    public static String getPassword(Context ctx)
    {
        SecurePreferences securePrefs = new SecurePreferences(ctx);
        return securePrefs.getString(Constants.PASSWORD, "");
    }
}
