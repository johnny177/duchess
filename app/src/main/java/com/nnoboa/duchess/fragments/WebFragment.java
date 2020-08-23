package com.nnoboa.duchess.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.BlogActivity;
import com.nnoboa.duchess.controllers.ThemeUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WebFragment extends Fragment {

    TextView blogText, chatText;

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        findView(view);
        startBlogActivity(blogText);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int selected = Integer.parseInt(preferences.getString("theme", "1"));
        switch (selected) {
            case ThemeUtils.DARK_THEME:
                view.setBackgroundColor(Color.BLACK);
                break;
            case ThemeUtils.DEFAULT_THEME:
                view.setBackgroundColor(getContext().getColor(R.color.web_frag_background));
                break;
        }
        return view;
    }

    private void findView(View view) {
        blogText = view.findViewById(R.id.blog_web_text);
        chatText = view.findViewById(R.id.chat_web_text);
    }

    private void startBlogActivity(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BlogActivity.class);
                startActivity(intent);
            }
        });

    }
}