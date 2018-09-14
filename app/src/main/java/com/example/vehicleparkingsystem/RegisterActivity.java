package com.example.vehicleparkingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vehicleparkingsystem.utils.EntryValidation;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // show back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button registerButton;
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEntry();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void validateEntry(){
        EditText userNameEdit, emailEdit, passwordEdit, confirmPasswordEdit, carPlateEdit;

        userNameEdit = findViewById(R.id.editUserName);
        emailEdit = findViewById(R.id.editEmail);
        passwordEdit = findViewById(R.id.editPassword);
        confirmPasswordEdit = findViewById(R.id.editConfirmPassword);
        carPlateEdit = findViewById(R.id.editCarPlate);

        EntryValidation validate = new EntryValidation();

        if(validate.validateUserName(userNameEdit)
                && validate.validateEmail(emailEdit)
                && validate.validatePassword(passwordEdit)
                && validate.validateConfirmPassword(passwordEdit, confirmPasswordEdit)
                && validate.validateCarPlateNumber(carPlateEdit)){
            Log.i("ksk_tag", "ok");
        }
    }
}
