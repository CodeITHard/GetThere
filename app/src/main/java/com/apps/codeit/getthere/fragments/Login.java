package com.apps.codeit.getthere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apps.codeit.getthere.R;
import com.apps.codeit.getthere.activities.LoginRegister;
import com.apps.codeit.getthere.activities.MainMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Login extends Fragment implements View.OnClickListener {

    Button login_button_login;
    EditText login_email_input, login_password_input;

    public Login(){
        // Requires public empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);

        login_email_input = view.findViewById(R.id.login_email_input);
        login_password_input = view.findViewById(R.id.login_password_input);

        login_button_login = view.findViewById(R.id.login_button_login);
        login_button_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button_login:
                login(login_email_input.getText().toString().trim(), login_password_input.getText().toString().trim());
                break;
        }
    }

    private void login(String email, String password) {
        if(!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password)){
            LoginRegister.firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getActivity(), MainMap.class));
                                getActivity().finish();
                            }
                            else {
                                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                                login_email_input.startAnimation(shake);
                                login_password_input.startAnimation(shake);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(getContext(), "Please fill out the fields", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            login_email_input.startAnimation(shake);
            login_password_input.startAnimation(shake);
        }

    }
}
