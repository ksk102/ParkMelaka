package com.example.vehicleparkingsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vehicleparkingsystem.utils.EntryValidation;
import com.example.vehicleparkingsystem.utils.HashingAlgorithm;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements OnTaskCompleted {

    ProgressBar progressBar;
    EditText userNameEdit, emailEdit, passwordEdit, confirmPasswordEdit, carPlateEdit;

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
        progressBar = findViewById(R.id.progressBar);
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
            validateEmailExists(emailEdit);
        }
    }

    private void validateEmailExists(EditText emailEdit){
        String email = emailEdit.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        //Calling the getUserPassword API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CHECK_EMAIL_EXISTS, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    public void onTaskCompleted(JSONObject object){
        String callback = "";

        try
        {
            callback = object.getString("callback");
        }
        catch (JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        switch (callback) {
            case "checkEmailExists":

                String success = "";
                String exists = "";

                try{
                    success = object.getString("success");
                    exists = object.getString("exists");
                }
                catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if(success.equals("1")){

                    if(exists.equals("0")){
                        registerUser();
                    }
                    else{
                        emailEdit.requestFocus();
                        emailEdit.setError("This email is already registered");
                    }
                }

                break;

            case "createUser":

                success = "";

                try{
                    success = object.getString("success");
                }
                catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if(success.equals("1")){
                    Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void registerUser(){
        String name = userNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();
        String carPlate = carPlateEdit.getText().toString();

        // hash the password
        password = HashingAlgorithm.MD5(password);

        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("carPlate", carPlate);

        //Calling the getUserPassword API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_USER, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }
}
