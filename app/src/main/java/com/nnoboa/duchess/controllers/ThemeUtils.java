package com.nnoboa.duchess.controllers;

import android.app.Activity;
import android.content.Intent;

import com.nnoboa.duchess.MainActivity;
import com.nnoboa.duchess.R;

public class ThemeUtils {

    public final static int DEFAULT_THEME = 0;
    public final static int DARK_THEME = 1;
    private static int mTheme;

    public static void setTheme(Activity activity, int theme) {
        mTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (mTheme) {
            case DARK_THEME:
                activity.setTheme(R.style.AppTheme);
                break;
            case DEFAULT_THEME:
                activity.setTheme(R.style.DarkTheme);
                break;
        }
    }
}
