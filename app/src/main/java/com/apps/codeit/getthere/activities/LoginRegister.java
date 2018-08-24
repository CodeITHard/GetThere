package com.apps.codeit.getthere.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.adapters.ViewPagerAdapter;
import com.apps.codeit.getthere.fragments.Login;
import com.apps.codeit.getthere.fragments.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginRegister extends AppCompatActivity {

    Toolbar toolbar;

    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser currentUser;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        sharedPreferences = this.getSharedPreferences("Token", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(TextUtils.isEmpty(sharedPreferences.getString("token", ""))){
            String token = FirebaseInstanceId.getInstance().getToken();
            editor.putString("token", token);
            editor.apply();
        }

        toolbar = findViewById(R.id.login_register_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();



        ViewPager viewPager = findViewById(R.id.login_register_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.login_register_tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, MainMap.class));
            finish();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Login(), "Login");
        adapter.addFragment(new Register(), "Register");
        viewPager.setAdapter(adapter);
    }
}
