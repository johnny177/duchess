package com.nnoboa.duchess;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.duchess.controllers.alarm.AlarmStarter;
import com.nnoboa.duchess.controllers.alarm.Util;
import com.nnoboa.duchess.fragments.AlarmFragment;
import com.nnoboa.duchess.fragments.DocFragment;
import com.nnoboa.duchess.fragments.FlashFragment;
import com.nnoboa.duchess.fragments.WebFragment;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        this.context = this;

        AlarmStarter.init(context);

        Util.scheduleJob(this);


//        QueryDb();


        //find the bottom navigation from the xml
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

    @Override
    protected void onStart() {
        super.onStart();
        Util.scheduleJob(this);
        AlarmStarter.init(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlarmStarter.init(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmStarter.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmStarter.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmStarter.init(this);
    }
}