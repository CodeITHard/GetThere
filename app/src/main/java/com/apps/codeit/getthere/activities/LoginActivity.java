package com.apps.codeit.getthere.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.codeit.getthere.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button login_button_login;
    EditText login_email_input, login_password_input;
    TextView login_edittext_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences = this.getSharedPreferences("Token", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(TextUtils.isEmpty(sharedPreferences.getString("token", ""))){
            String token = FirebaseInstanceId.getInstance().getToken();
            editor.putString("token", token);
            editor.apply();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        login_email_input = findViewById(R.id.login_email_input);
        login_password_input = findViewById(R.id.login_password_input);
        login_password_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(login_email_input.getText().toString().trim(), login_password_input.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        login_button_login = findViewById(R.id.login_button_login);
        login_button_login.setOnClickListener(this);

        login_edittext_signup = findViewById(R.id.login_edittext_signup);
        login_edittext_signup.setOnClickListener(this);


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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button_login:
                login(login_email_input.getText().toString().trim(), login_password_input.getText().toString().trim());
                break;
            case R.id.login_edittext_signup:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

        }
    }

    private void login(String email, String password) {
        if(!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(), MainMap.class));
                                finish();
                            }
                            else {
                                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                login_email_input.startAnimation(shake);
                                login_password_input.startAnimation(shake);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            login_email_input.startAnimation(shake);
            login_password_input.startAnimation(shake);
        }

    }
}
