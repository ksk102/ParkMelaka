package com.example.vehicleparkingsystem.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vehicleparkingsystem.R;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<ParkingHistory> {
    public HistoryAdapter(Context context, ArrayList<ParkingHistory> history) {
        super(context, 0, history);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ParkingHistory history = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_history, parent, false);
        }

        // Lookup view for data population
        TextView startDateText = convertView.findViewById(R.id.textItemDate);
        TextView startTimeText = convertView.findViewById(R.id.textItemTime);
        TextView locationText = convertView.findViewById(R.id.textItemLocation);

        // Populate the data into the template view using the data object
        startDateText.setText(history.startDate);
        startTimeText.setText(history.startTime);
        locationText.setText(history.location);

        // Return the completed view to render on screen
        return convertView;
    }
}
