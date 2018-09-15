package com.example.vehicleparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.example.vehicleparkingsystem.utils.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ParkingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskCompleted, ParkingFragment.FragmentCallBack, ParkedFragment.FragmentCallBack, HistoryFragment.FragmentCallBack, HistoryDetailFragment.FragmentCallBack {

    private ProgressBar progressBar;
    private TextView userNameText, carNumberText, balanceText;
    private ParkingFragment parkingFragment;
    private HistoryFragment historyFragment;
    private ParkedFragment parkedFragment;
    private HistoryDetailFragment historyDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // Header Navigation Bar
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
        parkedFragment = new ParkedFragment();
        historyDetailFragment = new HistoryDetailFragment();

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
        if(SaveSharedPreference.getStartTimeExists(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "You need to End Parking first before logout!", Toast.LENGTH_LONG).show();
        }
        else{
            SaveSharedPreference.setLoggedIn(getApplicationContext(), false);
            SaveSharedPreference.setStartTimeExists(getApplicationContext(), false);

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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

            parkingFragment.showUserDetail(carNumber, balance);
        }
        else{
            logout();
        }
    }

    private void displayParkingFragment() {

        setTitle("ParkMelaka");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (parkingFragment.isAdded()) { // if the fragment is already in container
            ft.show(parkingFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, parkingFragment, "A");
        }
        // Hide fragment history
        if (historyFragment.isAdded()) { ft.hide(historyFragment); }
        if (parkedFragment.isAdded()) { ft.hide(parkedFragment); }
        if (historyDetailFragment.isAdded()) { ft.hide(historyDetailFragment); }
        // Commit changes
        ft.commitNow();
    }

    private void displayHistoryFragment() {

        setTitle("Parking History");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (historyFragment.isAdded()) { // if the fragment is already in container
            historyFragment.getHistoryList();
            ft.show(historyFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, historyFragment, "B");
        }
        // Hide fragment parking
        if (parkingFragment.isAdded()) { ft.hide(parkingFragment); }
        if (parkedFragment.isAdded()) { ft.hide(parkedFragment); }
        if (historyDetailFragment.isAdded()) { ft.hide(historyDetailFragment); }
        // Commit changes
        ft.commitNow();
    }

    private void displayParkedFragment(){

        setTitle("Payment Summary");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (parkedFragment.isAdded()) { // if the fragment is already in container
            ft.show(parkedFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, parkedFragment, "C");
        }
        // Hide fragment parking
        if (parkingFragment.isAdded()) { ft.hide(parkingFragment); }
        if (historyFragment.isAdded()) { ft.hide(historyFragment); }
        if (historyDetailFragment.isAdded()) { ft.hide(historyDetailFragment); }
        // Commit changes
        ft.commitNow();
    }

    private void displayHistoryDetailFragment(){

        setTitle("Parking History");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (historyDetailFragment.isAdded()) { // if the fragment is already in container
            ft.show(historyDetailFragment);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.content_frame, historyDetailFragment, "D");
        }
        // Hide fragment parking
        if (parkingFragment.isAdded()) { ft.hide(parkingFragment); }
        if (historyFragment.isAdded()) { ft.hide(historyFragment); }
        if (parkedFragment.isAdded()) { ft.hide(parkedFragment); }
        // Commit changes
        ft.commitNow();
    }

    @Override
    public void fragmentCallBack(JSONObject object) {
        displayParkedFragment();

        parkedFragment.endTransactionCallBack(object);
    }

    @Override
    public void enableOkButton(){
        displayParkingFragment();
    }

    @Override
    public void refreshBalance(double balance){
        this.balanceText.setText("Balance: RM" + String.format("%.2f", balance));

        parkingFragment.refreshBalance(balance);
    }

    @Override
    public void resetParkingFragment(){
        parkingFragment.resetEntry();
    }

    @Override
    public void displayHistoryDetail(JSONObject object){
        displayHistoryDetailFragment();

        historyDetailFragment.getDetail(object);
    }

    @Override
    public void enableDetailOkButton(){
        displayHistoryFragment();
    }
}
