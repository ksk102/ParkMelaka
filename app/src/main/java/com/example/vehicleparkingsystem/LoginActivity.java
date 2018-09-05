package com.example.vehicleparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vehicleparkingsystem.utils.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements OnTaskCompleted{
    private EditText emailEdit, passwordEdit;
    private TextView errorText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton;
        RelativeLayout loginForm;

        loginButton = findViewById(R.id.buttonLogin);
        emailEdit = findViewById(R.id.editEmail);
        passwordEdit = findViewById(R.id.editPassword);
        errorText  = findViewById(R.id.textError);
        progressBar = findViewById(R.id.progressBar);
        loginForm = findViewById(R.id.formLogin);

        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), ParkingActivity.class);
            startActivity(intent);
        }
        else {
            loginForm.setVisibility(View.VISIBLE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyLogin();
            }
        });
    }

    private void verifyLogin(){
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        //Calling the getUserPassword API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_GET_USER_PASSWORD, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    public void onTaskCompleted(JSONObject object){
        String success = "";
        String userExist = "";
        String message = "";

        try{
            success = object.getString("success");
            message = object.getString("message");
            try{
                userExist = object.getString("user_exist");
            }
            catch(JSONException e){
                userExist = null;
            }
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if(success.equals("1")){
            if(userExist != null){

                SaveSharedPreference.setLoggedIn(getApplicationContext(), true);

                Intent intent=new Intent(this,ParkingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                Bundle bundle = new Bundle();
                bundle.putString("userId", userExist);
                intent.putExtras(bundle);
                this.startActivity(intent);

                this.errorText.setText("");
                this.errorText.setVisibility(View.GONE);
            }
            else{
                this.errorText.setText(message);
                this.errorText.setVisibility(View.VISIBLE);
            }
        }
        else{
            this.errorText.setText(message);
            this.errorText.setVisibility(View.VISIBLE);
        }
    }
}
