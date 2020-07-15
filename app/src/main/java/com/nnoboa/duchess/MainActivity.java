package com.nnoboa.duchess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.duchess.controllers.alarm.AlarmReceiver;
import com.nnoboa.duchess.controllers.alarm.ScheduleAlarmStarter;
import com.nnoboa.duchess.controllers.alarm.Util;
import com.nnoboa.duchess.data.AlarmContract;
import com.nnoboa.duchess.fragments.AlarmFragment;
import com.nnoboa.duchess.fragments.DocFragment;
import com.nnoboa.duchess.fragments.FlashFragment;
import com.nnoboa.duchess.fragments.WebFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        this.context = this;
        ScheduleAlarmStarter.startAlarm(context);

        Util.scheduleJob(this);


//        QueryDb();


        //fint the bottom navigation from the xml
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //check if the savedState is null and set the alarm Frag as the home frag

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new AlarmFragment()).commit();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_alarm:
                            selectedFragment = new AlarmFragment();
                            break;
                        case R.id.nav_reading_list:
                            selectedFragment = new DocFragment();
                            break;
                        case R.id.nav_flashcard:
                            selectedFragment = new FlashFragment();
                            break;
                        case R.id.nav_web:
                            selectedFragment = new WebFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, selectedFragment).commit();

                    return true;
                }
            };

}