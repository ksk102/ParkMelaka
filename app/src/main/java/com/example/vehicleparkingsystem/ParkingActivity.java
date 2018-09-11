package com.example.vehicleparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vehicleparkingsystem.utils.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ParkingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskCompleted {

    private ProgressBar progressBar;
    private TextView userNameText, carNumberText, balanceText;
    private ParkingFragment parkingFragment;
    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Header Nagigation Bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerNavView = navigationView.getHeaderView(0);

        // Header Navigation Bar items
        userNameText = headerNavView.findViewById(R.id.textUserName);
        carNumberText = headerNavView.findViewById(R.id.textCarNumber);
        balanceText = headerNavView.findViewById(R.id.textBalance);

        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set the item selected on nav bar
        navigationView.setCheckedItem(R.id.nav_parking);
        navigationView.setNavigationItemSelectedListener(this);

        // get user's details from database
        getUserDetail();

        // show the content
        parkingFragment = new ParkingFragment();
        historyFragment = new HistoryFragment();
        displayParkingFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_parking) {
            setTitle("ParkMelaka");
            displayParkingFragment();
        }
        else if (id == R.id.nav_history) {
            setTitle("Parking History");
            displayHistoryFragment();
        }
        else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        SaveSharedPreference.setLoggedIn(getApplicationContext(), false);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void getUserDetail(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String userId = null;

        if(bundle != null){
            if(bundle.containsKey("userId")) {
                userId = bundle.getString("userId");
            }
        }
        else{
            userId = SaveSharedPreference.getLoggedId(getApplicationContext());
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userId);

        //Calling the getUserDetail API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_GET_USER_DETAIL, params, GlobalConstants.CODE_POST_REQUEST, progressBar, this);
        request.execute();
    }

    public void onTaskCompleted(JSONObject object) {
        String success = "";
        JSONObject userDetail;
        String userName = "";
        String carNumber = "";
        String balance = "";

        try{
            success = object.getString("success");
            userDetail = new JSONObject(object.getString("user_detail"));

            userName = userDetail.getString("user_name");
            carNumber = userDetail.getString("car_number");
            balance = userDetail.getString("user_balance");
        }
        catch(JSONException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        if(success.equals("1")){
            this.userNameText.setText(userName);
            this.carNumberText.setText("Car Number: " + carNumber);
            this.balanceText.setText("Balance: RM" + balance);

            ParkingFragment fragmentParking = (ParkingFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            fragmentParking.showUserDetail(carNumber, balance);
        }
        else{
            logout();
        }
    }

    private void displayParkingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (parkingFragment.isAdded()) { // if the fragment is already in container
            ft.show(parkingFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, parkingFragment, "A");
        }
        // Hide fragment history
        if (historyFragment.isAdded()) { ft.hide(historyFragment); }
        // Commit changes
        ft.commit();
    }

    private void displayHistoryFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (historyFragment.isAdded()) { // if the fragment is already in container
            ft.show(historyFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, historyFragment, "A");
        }
        // Hide fragment parking
        if (parkingFragment.isAdded()) { ft.hide(parkingFragment); }
        // Commit changes
        ft.commit();
    }
}
