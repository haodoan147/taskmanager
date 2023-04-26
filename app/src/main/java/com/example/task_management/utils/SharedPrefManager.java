package com.example.task_management.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.task_management.activity.SignInActivity;
import com.example.task_management.model.AccessToken;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "ATAuthen";
    private static final String KEY_ACCESSTOKEN = "keyaccesstoken";

    private static SharedPrefManager mInstance;

    private static Context ctx;

    public SharedPrefManager(Context context) {
        ctx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public void userLogin(String accessToken){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESSTOKEN, accessToken);
        editor.apply();
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESSTOKEN, null) != null;
    }

    public AccessToken getAccessToken(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new AccessToken(
                sharedPreferences.getString(KEY_ACCESSTOKEN, "")
        );
    }
    public void logout(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
//        ctx.startActivity(new Intent(ctx, SignInActivity.class));
    }
}
