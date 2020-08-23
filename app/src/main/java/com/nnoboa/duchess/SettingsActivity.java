package com.nnoboa.duchess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.nnoboa.duchess.controllers.ThemeUtils;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    static int REQUEST_CODE_ALERT_RINGTONE = 100;
    static Preference preference;
    static ListPreference listPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.d("ActivityResult", ringtone.toString());
            if (ringtone != null) {
                preference.setSummary(RingtoneManager.getRingtone(getApplicationContext(), ringtone).getTitle(getApplicationContext()));
                setRingtonePreferenceValue(ringtone.toString());
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue("");
            }
        } else {
            Toast.makeText(getApplicationContext(), "RingTone not set", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRingtonePreferenceValue(String toString) {
        Toast.makeText(getApplicationContext(), "Ringtone set to " + RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(toString)).getTitle(getApplicationContext()), Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor1;
        editor1 = preference.getSharedPreferences().edit();
        editor1.putString("general_ringtone", toString);
        editor1.commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            preference = findPreference("general_ringtone");
            listPreference = findPreference("theme");
            assert listPreference != null;
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int
                            selectedTheme =
                            Integer.parseInt(Objects.requireNonNull(preference.getSharedPreferences().getString("theme", "0")));
                    ThemeUtils.setTheme(getActivity(), selectedTheme);
                    return true;
                }
            });
            preference.setSummary(RingtoneManager.getRingtone(getContext(), Uri.parse(getRingtonePreferenceValue())).getTitle(getContext()));
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (preference.getKey().equals("general_ringtone")) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                Uri
                        ringtone =
                        RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);
                String existingValue = getRingtonePreferenceValue();
                Log.d("RingtonePreference", getRingtonePreferenceValue());
                if (existingValue != null) {
                    if (existingValue.length() == 0) {
                        // Select "Silent"
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                    }
                } else {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                }

                startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
                return true;
            } else {
                return super.onPreferenceTreeClick(preference);
            }
        }

        private String getRingtonePreferenceValue() {
            String
                    preferredRingtone =
                    preference.getSharedPreferences().getString("general_ringtone", "content://settings/system/alarm_sound");
            return preferredRingtone;

        }
    }

}