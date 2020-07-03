package com.kl.cameraapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister){
            if(TextUtils.isEmpty(username.getText().toString())){
                Toast.makeText(LoginForm.this, "Username can not be blank!", Toast.LENGTH_LONG).show();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(username.getText().toString()).matches()){
                Toast.makeText(LoginForm.this, "Email address is not valid!", Toast.LENGTH_SHORT).show();
            }
            else  if(TextUtils.isEmpty(password.getText().toString())){
                Toast.makeText(LoginForm.this, "Password can not be blank!", Toast.LENGTH_SHORT).show();
            }
            else if(password.getText().toString().length() < 6){
                Toast.makeText(LoginForm.this, "Password need to have 6 or more characters", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginForm.this, "Registration", Toast.LENGTH_SHORT).show();
                registration();
            }
        }
        if(view == btnLogin){
            if(TextUtils.isEmpty(username.getText().toString())){
                Toast.makeText(LoginForm.this, "Username can not be blank!", Toast.LENGTH_LONG).show();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(username.getText().toString()).matches()){
                Toast.makeText(LoginForm.this, "Email address is not valid!", Toast.LENGTH_SHORT).show();
            }
            else  if(TextUtils.isEmpty(password.getText().toString())){
                Toast.makeText(LoginForm.this, "Password can not be blank!", Toast.LENGTH_SHORT).show();
            }
            else if(password.getText().toString().length() < 6){
                Toast.makeText(LoginForm.this, "Password need to have 6 or more characters", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginForm.this, "Start to Login...", Toast.LENGTH_SHORT).show();
                firebaseAuthWithEmail();
            }
        }
    }
    // bi the lz nao y

    private void registration() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("email", username.getText().toString());
                        map.put("password", password.getText().toString());
                        Toast.makeText(LoginForm.this, "Created", Toast.LENGTH_SHORT).show();

//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(FirebaseAuth.getInstance().getUid())
//                                .setValue(map)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(LoginForm.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
                        Toast.makeText(LoginForm.this, "Login Successful.... ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginForm.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginForm.this, "Failed :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}