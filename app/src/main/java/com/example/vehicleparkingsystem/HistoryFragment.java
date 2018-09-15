package com.example.vehicleparkingsystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.vehicleparkingsystem.utils.SaveSharedPreference;
import com.example.vehicleparkingsystem.utils.adapter.HistoryAdapter;
import com.example.vehicleparkingsystem.utils.adapter.ParkingHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryFragment extends Fragment implements OnTaskCompleted {

    ArrayList<ParkingHistory> arrayOfHistory;
    HistoryAdapter adapter;

    public HistoryFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        View historyHeader = getLayoutInflater().inflate(R.layout.header_history, null);

        // Construct the data source
        arrayOfHistory = new ArrayList<>();

        // Create the adapter to convert the array to views
        adapter = new HistoryAdapter(getActivity(), arrayOfHistory);

        // Attach the adapter to a ListView
        ListView listView = getActivity().findViewById(R.id.listHistory);
        listView.setAdapter(adapter);
        listView.addHeaderView(historyHeader);

        getHistoryList();
    }

    public void getHistoryList(){

        ProgressBar progressBar = getActivity().findViewById(R.id.progressBar);
        String userId = SaveSharedPreference.getLoggedId(getContext());


        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);

        //Calling the getLocation API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_GET_HISTORY_LIST, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    @Override
    public void onTaskCompleted(JSONObject object){
        String callback = "";
        String success = "";

        try
        {
            callback = object.getString("callback");
        }
        catch (JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        switch (callback) {
            case "getHistoryList":
                try {
                    success = object.getString("success");
                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                if (success.equals("1")) {
                    try {
                        // Get and store decoded array of business results
                        JSONArray HistoryJArray = object.getJSONArray("historyList");
                        arrayOfHistory.clear(); // clear existing items if needed
                        arrayOfHistory.addAll(ParkingHistory.fromJson(HistoryJArray)); // add new items
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                    }
                }

                break;
        }
    }
}