package com.nnoboa.duchess.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import com.nnoboa.duchess.R;

public class ReminderActivity extends AppCompatActivity {

    ExtendedFloatingActionButton addReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //find the resp views
        findViews();

        //start editor activity
        startEditorIntent();

    }

    /**
     * Find the respective views
     */

    private void findViews(){
        addReminder = findViewById(R.id.add_reminder);
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent(){

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(ReminderActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }
}