package com.apps.codeit.getthere.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", refreshedToken);

        sharedPreferences = getApplication().getSharedPreferences("Token", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("token", refreshedToken);
        editor.apply();
    }
}
