package com.apps.codeit.getthere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.listeners.MyTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private TextInputLayout register_password_input_layout, register_email_input_layout;
    private EditText register_password_input, register_email_input;

    private Button register_button_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        firebaseAuth = FirebaseAuth.getInstance();

        register_password_input_layout = findViewById(R.id.register_password_input_layout);
        register_password_input = findViewById(R.id.register_password_input);

        register_email_input_layout = findViewById(R.id.register_email_input_layout);
        register_email_input = findViewById(R.id.register_email_input);

        register_password_input.addTextChangedListener(new MyTextWatcher(register_password_input, register_password_input_layout));
        register_password_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerUser(register_email_input.getText().toString().trim(), register_password_input.getText().toString().trim());
                    register_email_input.setText("");
                    register_password_input.setText("");
                    return true;
                }
                return false;
            }
        });

        register_button_register = findViewById(R.id.register_button_register);
        register_button_register.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_button_register:
                // Calling the create user method when clicking the button
                registerUser(register_email_input.getText().toString().trim(), register_password_input.getText().toString().trim());
                register_email_input.setText("");
                register_password_input.setText("");
                break;
        }
    }

    // The create user method with email and password as parameters
    private void registerUser(final String email, final String password) {
        if(!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password)){
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()){
                                                    startActivity(new Intent(getApplicationContext(), MainMap.class));
                                                    finish();
                                                }
                                                else {
                                                    //
                                                }
                                            }
                                        });
                            }
                            else {
                                //
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
        }

    }
}
