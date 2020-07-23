package com.nnoboa.duchess.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.ReminderActivity;
import com.nnoboa.duchess.activities.ScheduleActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {

    TextView reminderText, scheduleText;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_alarm, container, false);

        //call the method to findviews
        findViews(view);

        //call the startIntent methods to start resp activities
        startReminderIntent(reminderText);
        startScheduleIntent(scheduleText);

        // Inflate the layout for this fragment
        return view;

    }

    /**
     * Method to find the resp views
     */

    private void findViews(View view){
        reminderText = view.findViewById(R.id.reminder_alarm_text);
        scheduleText = view.findViewById(R.id.schedule_alarm_text);
    }

    /**
     * start Respective intents
     */

    private void startReminderIntent(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reminderIntent = new Intent(getContext(), ReminderActivity.class);
                startActivity(reminderIntent);
            }
        });

    }

    private void startScheduleIntent(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scheduleIntent = new Intent(getContext(), ScheduleActivity.class);
                startActivity(scheduleIntent);
            }
        });
    }
}