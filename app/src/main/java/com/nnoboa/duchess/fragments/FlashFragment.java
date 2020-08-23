package com.nnoboa.duchess.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.controllers.ThemeUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlashFragment extends Fragment {

    public FlashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flash, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int selected = Integer.parseInt(preferences.getString("theme", "1"));
        switch (selected) {
            case ThemeUtils.DARK_THEME:
                view.setBackgroundColor(Color.BLACK);
                break;
            case ThemeUtils.DEFAULT_THEME:
                view.setBackgroundColor(getContext().getColor(R.color.frag_default_background_color));
                break;
        }
        return view;
    }
}