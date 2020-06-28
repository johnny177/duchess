package com.nnoboa.duchess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nnoboa.duchess.fragments.AlarmFragment;
import com.nnoboa.duchess.fragments.DocFragment;
import com.nnoboa.duchess.fragments.FlashFragment;
import com.nnoboa.duchess.fragments.WebFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fint the bottom navigation from the xml
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        //check if the savedstate is null and set the alarm Frag as the home frag

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,new AlarmFragment()).commit();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment =null;

                    switch (menuItem.getItemId()){
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

                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,selectedFragment).commit();

                    return true;
                }
            };

}