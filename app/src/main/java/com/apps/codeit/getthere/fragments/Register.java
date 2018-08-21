package com.apps.codeit.getthere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.activities.LoginRegister;
import com.apps.codeit.getthere.activities.MainMap;
import com.apps.codeit.getthere.listeners.MyTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Register extends Fragment implements View.OnClickListener{

    private TextInputLayout register_password_input_layout, register_email_input_layout;
    private EditText register_password_input, register_email_input;

    private Button register_button_register;

    public Register(){
        // Requires public empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        register_password_input_layout = view.findViewById(R.id.register_password_input_layout);
        register_password_input = view.findViewById(R.id.register_password_input);

        register_email_input_layout = view.findViewById(R.id.register_email_input_layout);
        register_email_input = view.findViewById(R.id.register_email_input);

        register_password_input.addTextChangedListener(new MyTextWatcher(register_password_input, register_password_input_layout));

        register_button_register = view.findViewById(R.id.register_button_register);
        /*
        if (register_user_name_input_layout.isErrorEnabled() ||
                register_password_input_layout.isErrorEnabled()){
            register_button_register.setEnabled(false);
        }
        else {
            register_button_register.setEnabled(true);
        }
        */
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
        LoginRegister.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            LoginRegister.firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(getActivity(), MainMap.class));
                                                getActivity().finish();
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
}
