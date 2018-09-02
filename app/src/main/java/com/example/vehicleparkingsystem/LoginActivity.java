package com.example.vehicleparkingsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;

public class LoginActivity extends AppCompatActivity{

    private Button loginButton;
    private EditText emailEdit, passwordEdit;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.buttonLogin);
        emailEdit = findViewById(R.id.editEmail);
        passwordEdit = findViewById(R.id.editPassword);
        errorText  = findViewById(R.id.textError);
    }

    public void loginPost(View view){
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();


        new SigninActivity(this, errorText).execute(email,password);
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }
}
