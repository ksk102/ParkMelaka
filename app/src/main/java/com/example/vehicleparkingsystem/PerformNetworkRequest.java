package com.example.vehicleparkingsystem;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//inner class to perform network request extending an AsyncTask
public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
    private String url; // the url where we need to send the request
    private HashMap<String, String> params; // the parameters
    private int requestCode; // the request code to define whether it is a GET or POST
    private ProgressBar progressBar;
    private OnTaskCompleted listener;

    // constructor to initialize values
    PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, ProgressBar progressBar, OnTaskCompleted listener) {
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
        this.progressBar = progressBar;
        this.listener = listener;
    }

    // when the task started displaying a progressbar
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }


    // this method will give the response from the request
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);

        try {
            JSONObject object = new JSONObject(s);
            listener.onTaskCompleted(object);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    //the network operation will be performed in background
    @Override
    protected String doInBackground(Void... voids) {
        RequestHandler requestHandler = new RequestHandler();

        if (requestCode == GlobalConstants.CODE_POST_REQUEST)
            return requestHandler.sendPostRequest(url, params);


        if (requestCode == GlobalConstants.CODE_GET_REQUEST)
            return requestHandler.sendGetRequest(url);

        return null;
    }
}