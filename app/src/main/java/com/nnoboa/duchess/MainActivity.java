package com.nnoboa.duchess;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.duchess.controllers.ThemeUtils;
import com.nnoboa.duchess.controllers.alarm.AlarmStarter;
import com.nnoboa.duchess.controllers.alarm.Util;
import com.nnoboa.duchess.fragments.AlarmFragment;
import com.nnoboa.duchess.fragments.DocFragment;
import com.nnoboa.duchess.fragments.FlashFragment;
import com.nnoboa.duchess.fragments.WebFragment;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_PERMISSIONS = 1;
    Context context;
    boolean boolean_permission;
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

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        this.context = this;

        AlarmStarter.init(context);

        Util.scheduleJob(this);
        fn_permission();


//        QueryDb();


        //find the bottom navigation from the xml
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int selected = Integer.parseInt(preferences.getString("theme", "1"));
        switch (selected) {
            case ThemeUtils.DARK_THEME:
                bottomNavigationView.setItemTextColor(ColorStateList.valueOf(R.color.colorPrimaryDarkTheme));
                break;
            case ThemeUtils.DEFAULT_THEME:
                bottomNavigationView.setItemTextColor(ColorStateList.valueOf(R.color.colorPrimaryDark));
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //check if the savedState is null and set the alarm Frag as the home frag

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new AlarmFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        Util.scheduleJob(this);
        AlarmStarter.init(this);
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onDestroy();
    }

    @Override
    public boolean onNavigateUp() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        return super.onNavigateUp();
    }

    @Override
    protected void onUserLeaveHint() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onUserLeaveHint();
    }

    @Override
    protected void onPostResume() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onPostResume();
    }


    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}