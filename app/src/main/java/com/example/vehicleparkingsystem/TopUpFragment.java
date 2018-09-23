package com.example.vehicleparkingsystem;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TopUpFragment extends Fragment implements OnTaskCompleted{

    private ProgressBar progressBar;
    private EditText amountEdit;
    private EditText emailEdit;
    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private FragmentCallBack listener;

    // Define the events that the fragment will use to communicate
    public interface FragmentCallBack {
        // This can be any number of events to be sent to the activity
        void  displayParkingActivity();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            listener = (FragmentCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TopUpFragment.FragmentCallBack");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_top_up_request, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button topUpButton;
        topUpButton = getActivity().findViewById(R.id.topUp_request);
        progressBar = getActivity().findViewById(R.id.progressBar);

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAmount();
            }
        });
    }



    private void validateAmount(){
        amountEdit = getActivity().findViewById(R.id.amount_topUp);
        emailEdit = getActivity().findViewById(R.id.email_topUp);

        String amount = amountEdit.getText().toString().trim();

        if(Double.parseDouble(amount) > 0) {
            validateEmailExists(emailEdit);
        }
        else {
            amountEdit.setError("Invalid amount");
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
            Log.e("log_tag", "Error parsing data 0" + e.toString());
        }

        if("checkEmailExists".equals(callback)) {

            String success = "";
            String exists = "";

            try {
                success = object.getString("success");
                exists = object.getString("exists");
            }
            catch (JSONException e) {
                Log.e("log_tag", "Error parsing data 1" + e.toString());
            }

            if (success.equals("1")) {

                if (exists.equals("1")) {
                    sendTopUpRequest();
                }
                else {
                    emailEdit.requestFocus();
                    emailEdit.setError("Email address not exists");
                }
            }
        }

        else if("topUp".equals(callback)) {
            String success = "";

            try {
                success = object.getString("success");
            }
            catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
            }

            if (success.equals("1")) {
                Toast.makeText(getActivity().getApplicationContext(), "Successfully Requested", Toast.LENGTH_SHORT).show();
                listener.displayParkingActivity();
            }
        }
    }

    private void sendTopUpRequest(){
        String amount = amountEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", amount);
        params.put("email", email);

        //Calling the topUp API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_TOP_UP_REQUEST, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();

    }
}
