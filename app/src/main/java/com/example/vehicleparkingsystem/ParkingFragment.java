package com.example.vehicleparkingsystem;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vehicleparkingsystem.utils.SaveSharedPreference;
import com.example.vehicleparkingsystem.utils.StringWithTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingFragment extends Fragment implements OnTaskCompleted{

    private View rootView;
    private TextView parkCarNumberText, parkBalanceText;
    private TextView timeElapsedText;
    private Spinner parkLocationSpinner;
    private ProgressBar progressBar;
    private Button startButton, endButton;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    HashMap<Integer, String> locHash = new HashMap<>();

    public ParkingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView = inflater.inflate(R.layout.fragment_parking, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        progressBar = getActivity().findViewById(R.id.progressBar);

        parkCarNumberText = getActivity().findViewById(R.id.textParkCarNumber);
        parkBalanceText = getActivity().findViewById(R.id.textParkBalance);
        parkLocationSpinner = getActivity().findViewById(R.id.spinnerParkLocation);
        startButton = getActivity().findViewById(R.id.buttonStart);
        endButton = getActivity().findViewById(R.id.buttonEnd);

        timeElapsedText = getActivity().findViewById(R.id.textTimeElapsed);

        getLocation();
    }

    @Override
    public void onStart(){
        super.onStart();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCounter();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                endCounter();
            }
        });
    }

    void showUserDetail(String carNumber, String balance){
            parkCarNumberText.setText(carNumber);
            parkBalanceText.setText(balance);
    }

    private void getLocation(){
        HashMap<String, String> params = new HashMap<>();
        params.put("", "");

        //Calling the getLocation API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_GET_LOCATION, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    @Override
    public void onTaskCompleted(JSONObject object) {

        String callback = "";

        try
        {
            callback = object.getString("callback");
        }
        catch (JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        switch (callback){
            case "getLocation":
                String success = "";
                JSONArray locationJArray = null;
                List<String> locationList = new ArrayList<>();

                try{
                    success = object.getString("success");
                    locationJArray = object.getJSONArray("locations");

                    // Spinner Drop down elements
                    for (int i = 0; i < locationJArray.length(); i++) {

                        JSONObject loc_object = locationJArray.getJSONObject(i);

                        //getting each location object
                        int locId = loc_object.getInt("id");
                        String locName = loc_object.getString("loc_name");
                        locHash.put(locId, locName);
                    }
                }
                catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if(success.equals("1")) {
//            parkLocationSpinner.setOnItemSelectedListener(this);

                    /* Create your ArrayList collection using StringWithTag instead of String. */
                    List<StringWithTag> itemList = new ArrayList<>();

                    /* Iterate through your original collection, in this case defined with an Integer key and String value. */
                    for (Map.Entry<Integer, String> entry : locHash.entrySet()) {
                        Integer key = entry.getKey();
                        String value = entry.getValue();

                        /* Build the StringWithTag List using these keys and values. */
                        itemList.add(new StringWithTag(value, key));
                    }

                    /* Set your ArrayAdapter with the StringWithTag, and when each entry is shown in the Spinner, .toString() is called. */
                    ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(getContext(), android.R.layout.simple_spinner_item, itemList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    parkLocationSpinner.setAdapter(spinnerAdapter);
                }

                    break;

            case "startTransaction":
                break;

            default:
                break;
        }
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        StringWithTag swt = (StringWithTag) parent.getItemAtPosition(position);
//        Integer key = (Integer) swt.tag;
//        Log.e("ksk_tag", key.toString());
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//    }

    private void startCounter(){
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        startButton.setVisibility(View.GONE);
        endButton.setVisibility(View.VISIBLE);
        parkLocationSpinner.setEnabled(false);

        String userId = SaveSharedPreference.getLoggedId(getContext());

        StringWithTag swt = (StringWithTag) parkLocationSpinner.getSelectedItem();
        Integer key = (Integer) swt.tag;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("parking_location", key.toString());

        //Calling the getLocation API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_START_TRANSACTION, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            timeElapsedText.setText(hours + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs) + " hours");
            customHandler.postDelayed(this, 1000);
        }
    };

    private void endCounter(){
        customHandler.removeCallbacks(updateTimerThread);

        startButton.setVisibility(View.VISIBLE);
        endButton.setVisibility(View.GONE);
        parkLocationSpinner.setEnabled(true);
    }
}