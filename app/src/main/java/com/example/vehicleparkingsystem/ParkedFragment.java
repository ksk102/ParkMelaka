package com.example.vehicleparkingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ParkedFragment extends Fragment implements OnTaskCompleted {

    private ProgressBar progressBar;
    private TextView parkedStartTimeText, parkedEndTimeText, parkedDurationText, parkedCarNumberText, parkedLocationText, parkedChargedText, parkedBalanceText;
    private Button parkedOkButton;
    private View rootView;
    private double balance = 0;
    private String userId = "";

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private FragmentCallBack listener;

    // Define the events that the fragment will use to communicate
    public interface FragmentCallBack {
        // This can be any number of events to be sent to the activity
        public void enableOkButton();
    }

    public ParkedFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            listener = (FragmentCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ParkedFragment.FragmentCallBack");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_parked, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = getActivity().findViewById(R.id.progressBar);

        parkedStartTimeText = rootView.findViewById(R.id.textParkedStartTime);
        parkedEndTimeText = rootView.findViewById(R.id.textParkedEndTime);
        parkedDurationText = rootView.findViewById(R.id.textParkedDuration);
        parkedCarNumberText = rootView.findViewById(R.id.textParkedCarNumber);
        parkedLocationText = rootView.findViewById(R.id.textParkedLocation);
        parkedChargedText = rootView.findViewById(R.id.textParkedCharged);
        parkedBalanceText = rootView.findViewById(R.id.textParkedBalance);

        parkedOkButton = rootView.findViewById(R.id.buttonParkedOk);
    }

    void endTransactionCallBack(JSONObject object){
        String success = "";
        JSONObject durationDetail;

        String startDate = "";
        String startTime = "";
        String endDate = "";
        String endTime = "";
        String carNumber = "";
        String location = "";

        long startTimeStamp = 0;
        long endTimeStamp = 0;
        long parkingDuration = 0;

        try{
            success = object.getString("success");
            durationDetail = new JSONObject(object.getString("time"));

            userId = durationDetail.getString("id");
            startDate = durationDetail.getString("sdate");
            startTime = durationDetail.getString("stime");
            endDate = durationDetail.getString("edate");
            endTime = durationDetail.getString("etime");
            carNumber = durationDetail.getString("carno");
            location = durationDetail.getString("loc");
            balance = durationDetail.getDouble("balance");
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if(success.equals("1")){

            try{
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m");
                Date date = formatter.parse(startDate + " " + startTime);
                startTimeStamp = date.getTime();

                date = formatter.parse(endDate + " " + endTime);
                endTimeStamp = date.getTime();
            }
            catch (ParseException e){
                Log.e("log_tag", "Error parsing datetime " + e.toString());
            }

            parkingDuration = endTimeStamp - startTimeStamp;

            parkedStartTimeText.setText(startDate + " " + startTime);
            parkedEndTimeText.setText(endDate + " " + endTime);
            parkedCarNumberText.setText(carNumber);
            parkedLocationText.setText(location);

            calcDuration(parkingDuration);
            calcCharges(parkingDuration);
        }
    }

    private void calcDuration(long duration){
        int secs = (int) (duration / 1000);
        int mins = secs / 60;
        int hours = mins / 60;

        mins = mins % 60;

        parkedDurationText.setText(hours + " hours " + String.format("%02d", mins) + " minutes");
    }

    private void calcCharges(long duration){
        int secs = (int) (duration / 1000);
        int mins = secs / 60;
        int hours = mins / 60;

        double amount = hours * 0.60;

        if((mins % 60) > 30){
            amount += 0.60;
        }
        else{
            amount += 0.40;
        }

        balance -= amount;

        parkedChargedText.setText("RM" + String.format("%.2f", amount));
        parkedBalanceText.setText("RM" + String.format("%.2f", balance));

        updateBalance(balance);
    }

    private void updateBalance(Double balance){
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("balance", balance.toString());

        //Calling the getLocation API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_BALANCE, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    @Override
    public void onTaskCompleted(JSONObject object){

        String success = "";

        try{
            success = object.getString("success");
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if(success.equals("1")){
            parkedOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    listener.enableOkButton();
                }
            });
        }
    }
}