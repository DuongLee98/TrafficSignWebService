package com.kl.cameraapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kl.cameraapp.MainActivity;
import com.kl.cameraapp.R;
import java.util.HashMap;
import java.util.Map;

public class LoginForm extends AppCompatActivity implements View.OnClickListener{
    Button btnLogin, btnRegister;
    EditText username, password;
    String registUsername, registPassWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.popup_register, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);

                Button btnCreate = popupView.findViewById(R.id.btn_create);
                EditText us, pw, cp;
                us = popupView.findViewById(R.id.edit_reg_user);
                pw = popupView.findViewById(R.id.edit_reg_pwd);
                cp = popupView.findViewById(R.id.edit_reg_conf);

                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pw.getText().toString().equals(cp.getText().toString())
                                && Patterns.EMAIL_ADDRESS.matcher(us.getText().toString()).matches()
                                &&!TextUtils.isEmpty(us.getText().toString())
                                && !TextUtils.isEmpty(pw.getText().toString())
                                && pw.getText().toString().length() >=6){
                            Toast.makeText(LoginForm.this, "Registration", Toast.LENGTH_LONG).show();
                            registUsername = us.getText().toString();
                            registPassWord = pw.getText().toString();
                            registration();
                            popupWindow.dismiss();
                        }
                        else{
                            Toast.makeText(LoginForm.this, "Invalid Input", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister){
//            if(TextUtils.isEmpty(username.getText().toString())){
//                Toast.makeText(LoginForm.this, "Username can not be blank!", Toast.LENGTH_LONG).show();
//            }
//            else if(!Patterns.EMAIL_ADDRESS.matcher(username.getText().toString()).matches()){
//                Toast.makeText(LoginForm.this, "Email address is not valid!", Toast.LENGTH_LONG).show();
//            }
//            else  if(TextUtils.isEmpty(password.getText().toString())){
//                Toast.makeText(LoginForm.this, "Password can not be blank!", Toast.LENGTH_LONG).show();
//            }
//            else if(password.getText().toString().length() < 6){
//                Toast.makeText(LoginForm.this, "Password need to have 6 or more characters", Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(LoginForm.this, "Registration", Toast.LENGTH_LONG).show();
//                registration();
//            }

        }
        if(view == btnLogin){
            if(TextUtils.isEmpty(username.getText().toString())){
                Toast.makeText(LoginForm.this, "Username can not be blank!", Toast.LENGTH_LONG).show();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(username.getText().toString()).matches()){
                Toast.makeText(LoginForm.this, "Email address is not valid!", Toast.LENGTH_LONG).show();
            }
            else  if(TextUtils.isEmpty(password.getText().toString())){
                Toast.makeText(LoginForm.this, "Password can not be blank!", Toast.LENGTH_LONG).show();
            }
            else if(password.getText().toString().length() < 6){
                Toast.makeText(LoginForm.this, "Password need to have 6 or more characters", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(LoginForm.this, "Start to Login...", Toast.LENGTH_LONG).show();
                firebaseAuthWithEmail();
            }
        }
    }
    // bi the lz nao y

    private void registration() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(registUsername,
                registPassWord)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("email", username.getText().toString());
                        map.put("password", password.getText().toString());
                        Toast.makeText(LoginForm.this, "Created", Toast.LENGTH_LONG).show();

//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(FirebaseAuth.getInstance().getUid())
//                                .setValue(map)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(LoginForm.this, "Registration Successful", Toast.LENGTH_LONG).show();
//                                    }
//                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("fail", e.getMessage());
                        Toast.makeText(LoginForm.this, "Failed :" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void firebaseAuthWithEmail() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = authResult.getUser();
                        Toast.makeText(LoginForm.this, "Login Successful.... ", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginForm.this, MainActivity.class);
                        intent.putExtra("user", user.getEmail());
                        startActivity(new Intent(LoginForm.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginForm.this, "Failed :" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}