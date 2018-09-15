package com.example.vehicleparkingsystem;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryDetailFragment extends Fragment {

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private FragmentCallBack listener;

    // Define the events that the fragment will use to communicate
    public interface FragmentCallBack {
        // This can be any number of events to be sent to the activity
        void enableDetailOkButton();
    }

    public HistoryDetailFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            listener = (FragmentCallBack) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement HistoryDetailFragment.FragmentCallBack");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_history, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button okButton;
        okButton = getActivity().findViewById(R.id.buttonDetailOk);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                listener.enableDetailOkButton();
            }
        });
    }

    void getDetail(JSONObject object){

        TextView startTimeText, endTimeText, carNumberText, locationText, amountText;

        String startDate = "";
        String startTime = "";
        String endDate = "";
        String endTime = "";
        String carNo = "";
        String location = "";
        String amount = "";

        try{
            startDate = object.getString("startDate");
            startTime = object.getString("startTime");
            endDate = object.getString("endDate");
            endTime = object.getString("endTime");
            carNo = object.getString("carNo");
            location = object.getString("location");
            amount = object.getString("amount");
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        startTimeText = getActivity().findViewById(R.id.textDetailStartTime);
        endTimeText = getActivity().findViewById(R.id.textDetailEndTime);

        carNumberText = getActivity().findViewById(R.id.textDetailCarNumber);
        locationText = getActivity().findViewById(R.id.textDetailLocation);
        amountText = getActivity().findViewById(R.id.textDetailCharged);

        startTimeText.setText(startDate + " " + startTime);
        endTimeText.setText(endDate + " " + endTime);
        carNumberText.setText(carNo);
        locationText.setText(location);
        amountText.setText("RM" + amount);

        showDuration(startDate, startTime, endDate, endTime);
    }

    private void showDuration(String startDate, String startTime, String endDate, String endTime){

        TextView durationText;
        durationText = getActivity().findViewById(R.id.textDetailDuration);

        long startTimeStamp = 0;
        long endTimeStamp = 0;

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

        long parkingDuration = endTimeStamp - startTimeStamp;

        int secs = (int) (parkingDuration / 1000);
        int mins = secs / 60;
        int hours = mins / 60;

        mins = mins % 60;

        durationText.setText(hours + " hours " + String.format("%02d", mins) + " minutes");
    }
}
