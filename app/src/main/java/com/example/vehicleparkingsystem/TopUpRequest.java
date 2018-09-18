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

public class TopUpRequest extends AppCompatActivity implements OnTaskCompleted{

    ProgressBar progressBar;
    EditText amountEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_request);

        // show back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button topUpButton;
        topUpButton = findViewById(R.id.topUp_request);

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAmount(amountEdit))
                    sendTopUpRequest();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private boolean validateAmount(EditText amountEdit){
        String amount = amountEdit.getText().toString().trim();
        if(Double.parseDouble(amount) > 0) {
            return true;
        }
        return false;
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

        if("topUp".equals(callback)) {

            String success = "";

            try {
                success = object.getString("success");
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            if (success.equals("1")) {
                Toast.makeText(getApplicationContext(), "Successfully Request", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void sendTopUpRequest(){
        String amount = amountEdit.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", amount);

        //Calling the getUserPassword API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_TOP_UP, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }
}
