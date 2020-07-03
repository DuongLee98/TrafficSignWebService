package com.kl.cameraapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.kl.cameraapp.R;

public class LoginForm extends AppCompatActivity {
    Button btnLogin;
    EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        btnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
    }
}