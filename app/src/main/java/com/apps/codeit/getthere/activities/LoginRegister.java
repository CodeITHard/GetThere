package com.apps.codeit.getthere.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.adapters.ViewPagerAdapter;
import com.apps.codeit.getthere.fragments.Login;
import com.apps.codeit.getthere.fragments.Register;

public class LoginRegister extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        toolbar = findViewById(R.id.login_register_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewPager = findViewById(R.id.login_register_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.login_register_tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Login(), "Login");
        adapter.addFragment(new Register(), "Register");
        viewPager.setAdapter(adapter);
    }
}
