package com.example.vehicleparkingsystem.utils.adapter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParkingHistory {

    public String startDate;
    public String startTime;
    public String endDate;
    public String endTime;
    public String carNo;
    public String location;
    public String amount;

    private ParkingHistory(){

    }

    private static ParkingHistory fromJson(JSONObject object) {
        ParkingHistory history = new ParkingHistory();
        // Deserialize json into object fields
        try {
            history.startDate = object.getString("startDate");
            history.startTime = object.getString("startTime");
            history.endDate = object.getString("endDate");
            history.endTime = object.getString("endTime");
            history.carNo = object.getString("carNo");
            history.location = object.getString("location");
            history.amount = object.getString("amount");
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            return null;
        }
        // Return new object
        return history;
    }

    public static ArrayList<ParkingHistory> fromJson(JSONArray jArray) {

        JSONObject object;
        ArrayList<ParkingHistory> historyList = new ArrayList<>(jArray.length());

        for (int i = 0; i < jArray.length(); i++) {
            try {
                object = jArray.getJSONObject(i);
            }
            catch (JSONException e) {
                Log.e("log_tag", "Error parsing data " + e.toString());
                continue;
            }

            ParkingHistory history = ParkingHistory.fromJson(object);
            if (history != null) {
                historyList.add(history);
            }
        }

        return historyList;
    }
}