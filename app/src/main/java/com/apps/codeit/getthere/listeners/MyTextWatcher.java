package com.apps.codeit.getthere.listeners;

import android.content.Context;
import android.location.Geocoder;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.apps.codeit.getthere.R;

public class MyTextWatcher implements TextWatcher {
    private EditText editText;
    private TextInputLayout textInputLayout;

    public MyTextWatcher(EditText editText, TextInputLayout textInputLayout){
        this.editText = editText;
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        switch (editText.getId()){
            case R.id.register_password_input:
                validatePassword(charSequence.toString().trim());
                break;
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private boolean validateUsername(String string) {
        if (string.length() < 5) {
            textInputLayout.setError("Username is too short");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword(String string) {
        if(string.isEmpty() || isValidPassword(string) == 1){
            textInputLayout.setError("Password is too short");
            return false;
        }
        else if(isValidPassword(string) == 2){
            textInputLayout.setError("Wrong password");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private int isValidPassword(String password){
        if(password.length() < 8){
            return 1;
        }
        else{
            for(int i=0;i<password.length();i++){
                String x = String.valueOf(password.toCharArray()[i]);
                if(isUnacceptableSymbol(x)){
                    return 2;
                }
            }
        }
        return 0;
    }
    private static boolean isUnacceptableSymbol(String c){
        return c.equals("!") || c.equals("@") || c.equals("#") || c.equals("$") || c.equals("%") || c.equals("^") || c.equals("&") ||
                c.equals("*") || c.equals("(") || c.equals(")") || c.equals("-") || c.equals("=") || c.equals("_") || c.equals("+") || c.equals(" ");
    }



}
