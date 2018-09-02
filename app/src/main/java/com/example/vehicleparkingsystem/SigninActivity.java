package com.example.vehicleparkingsystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AsyncTask<String, Void, String>{
    private Context context;
    private TextView errorText;

    public SigninActivity(Context context, TextView errorText) {
        this.context = context;
        this.errorText = errorText;
    }

    protected void onPreExecute(){
    }

    @Override
    protected String doInBackground(String... arg0){
        try {

            String username = arg0[0];
            String password = arg0[1];

            String link = "http://192.168.1.8/ParkMelaka_WebServices/user_find.php";
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader((new InputStreamReader(conn.getInputStream())));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            return sb.toString();
        }
        catch(Exception e){
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected  void onPostExecute(String result){
        JSONObject jArray = null;
        String success = null;
        String userExist = null;

        try{
            jArray = new JSONObject(result);
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        try{
            success = jArray.getString("success");
            userExist = jArray.getString("user_exist");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        if(success.equals("1")){
            if(userExist.equals("1")){
                Intent intent=new Intent(context,ParkingActivity.class);
                context.startActivity(intent);
                this.errorText.setVisibility(View.GONE);
            }
            else{
                this.errorText.setVisibility(View.VISIBLE);
            }
        }
    }
}
