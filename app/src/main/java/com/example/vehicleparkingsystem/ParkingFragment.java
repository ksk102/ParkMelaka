package com.example.vehicleparkingsystem;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private double balanceAmount;
    private View rootView;
    private TextView parkCarNumberText, parkBalanceText;
    private TextView timeElapsedText;
    private TextView timeTextText, startTimeTextText, timeText, startTimeText;
    private Spinner parkLocationSpinner;
    private TextView parkLocationText;
    private ProgressBar progressBar;
    private Button startButton, endButton;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    HashMap<Integer, String> locHash = new HashMap<>();

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private FragmentCallBack listener;

    // Define the events that the fragment will use to communicate
    public interface FragmentCallBack {
        // This can be any number of events to be sent to the activity
        public void fragmentCallBack(JSONObject object);
    }

    public ParkingFragment(){

    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            listener = (FragmentCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ParkingFragment.FragmentCallBack");
        }
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
        parkLocationText = getActivity().findViewById(R.id.textParkLocation);
        startButton = getActivity().findViewById(R.id.buttonStart);
        endButton = getActivity().findViewById(R.id.buttonEnd);

        startTimeTextText = getActivity().findViewById(R.id.textStartTimeText);
        timeTextText = getActivity().findViewById(R.id.textTimeText);
        startTimeText = getActivity().findViewById(R.id.textStartTime);
        timeText = getActivity().findViewById(R.id.textTime);

        timeElapsedText = getActivity().findViewById(R.id.textTimeElapsed);

        getLocation();
        ifParkingStarted();
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
        parkBalanceText.setText("RM" + balance);

        balanceAmount = Double.parseDouble(balance);
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
        String success = "";

        try
        {
            callback = object.getString("callback");
        }
        catch (JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        switch (callback){
            case "getLocation":

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
                try{
                    success = object.getString("success");
                }
                catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if(success.equals("1")){

                    startTime = SystemClock.uptimeMillis();
                    SaveSharedPreference.setStartTimeExists(getActivity(), true);
                    SaveSharedPreference.setStartTime(getActivity(), startTime);
                    SaveSharedPreference.setStartTime(getActivity(), timeText.getText().toString());
                    SaveSharedPreference.setParkingLocation(getActivity(), parkLocationSpinner.getSelectedItem().toString());

                    startTimeText.setText(timeText.getText());
                    parkLocationText.setText(parkLocationSpinner.getSelectedItem().toString());

                    setParkingStart();
                }

                break;

            case "endTransaction":
                try{
                    success = object.getString("success");
                }
                catch(JSONException e){
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if(success.equals("1")){
                    customHandler.removeCallbacks(updateTimerThread);

                    startButton.setVisibility(View.VISIBLE);
                    endButton.setVisibility(View.GONE);

                    listener.fragmentCallBack(object);
                }

                break;

            default:
                break;
        }
    }

    private void startCounter(){

        if(balanceAmount < 10){
            Toast.makeText(getActivity(), "You need to have a minimum of RM10 to start parking!", Toast.LENGTH_LONG).show();
        }
        else{
            /* API */
            // get user id
            String userId = SaveSharedPreference.getLoggedId(getContext());

            // get selected location id
            StringWithTag swt = (StringWithTag) parkLocationSpinner.getSelectedItem();
            Integer key = (Integer) swt.tag;

            // prepare parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("parking_location", key.toString());

            //Calling the startTransaction API
            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_START_TRANSACTION, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
            request.execute();
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            int hours = mins / 60;
            secs = secs % 60;
            mins = mins % 60;

            timeElapsedText.setText(hours + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 1000);
        }
    };

    private void endCounter(){
        /* API */
        // get user id
        String userId = SaveSharedPreference.getLoggedId(getContext());

        // prepare parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userId);

        //Calling the endTransaction API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_END_TRANSACTION, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    public void refreshBalance(double balance){
        parkBalanceText.setText("RM" + String.format("%.2f", balance));
    }

    public void resetEntry(){
        startTimeTextText.setVisibility(View.GONE);
        timeTextText.setVisibility(View.VISIBLE);
        startTimeText.setVisibility(View.GONE);
        timeText.setVisibility(View.VISIBLE);

        parkLocationSpinner.setVisibility(View.VISIBLE);
        parkLocationText.setVisibility(View.GONE);

        timeElapsedText.setText("0:00:00");

        SaveSharedPreference.setStartTimeExists(getActivity(), false);
    }

    private boolean isParkingStarted(){
        return SaveSharedPreference.getStartTimeExists(getActivity());
    }

    private void ifParkingStarted(){
        if(isParkingStarted()){
            startTime = SaveSharedPreference.getStartTime(getActivity());
            startTimeText.setText(SaveSharedPreference.getStartTimeString(getActivity()));
            parkLocationText.setText(SaveSharedPreference.getParkingLocation(getActivity()));

            setParkingStart();
        }
    }

    private void setParkingStart(){
        customHandler.postDelayed(updateTimerThread, 0);

        startButton.setVisibility(View.GONE);
        endButton.setVisibility(View.VISIBLE);
        parkLocationSpinner.setVisibility(View.GONE);
        parkLocationText.setVisibility(View.VISIBLE);

        startTimeTextText.setVisibility(View.VISIBLE);
        timeTextText.setVisibility(View.GONE);
        startTimeText.setVisibility(View.VISIBLE);
        timeText.setVisibility(View.GONE);
    }
}