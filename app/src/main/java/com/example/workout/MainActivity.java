package com.example.workout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Chronometer timer;
    ImageButton startButton, pauseButton, stopButton;
    EditText input;
    TextView message;
    long paused, current;
    boolean started;
    String workout, previousWorkout, timeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.simpleChronometer);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        message = findViewById(R.id.textView);
        input = findViewById(R.id.workoutType);

        // Load in data from shared preferences.
        load();

        paused = 0;
        message.setText(previousWorkout);

        // Check for saved instances.
        if (savedInstanceState != null) {
            paused = savedInstanceState.getLong("PAUSED");
            started = savedInstanceState.getBoolean("STARTED");
            current = savedInstanceState.getLong("CURRENT");

            if (started == true) {
                timer.start();
                timer.setBase(SystemClock.elapsedRealtime() - current);
            }
            else {
                timer.setBase(SystemClock.elapsedRealtime() - paused);
                timer.stop();
            }
        }
    }

    // Data to be saved on device rotation.
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("PAUSED", paused);
        outState.putBoolean("STARTED", started);
        outState.putLong("CURRENT", SystemClock.elapsedRealtime() - timer.getBase());
        outState.putString("MESSAGE", previousWorkout);
    }

    // Start timer
    public void start(View view) {
        if (!started) {
            started = true;
            timer.setBase(SystemClock.elapsedRealtime() - paused);
            timer.start();
        }
    }

    // Pause timer
    public void pause(View view) {
        if (started) {
            timer.stop();
            paused = SystemClock.elapsedRealtime() - timer.getBase();
            started = false;
        }
    }

    // Restarts timer, saves the current time and workout input to shared preferences and
    // puts them into the top message 'previous workout' message.
    public void restart(View view) {
        workout = input.getText().toString();
        convert();
        previousWorkout = String.format("You spent %s on %s last time.", timeDisplay, workout);
        message.setText(previousWorkout);
        save();
        timer.setBase(SystemClock.elapsedRealtime());
        paused = 0;
        started = false;
        timer.stop();
    }

    // Converts current time into a string formated as 'MM:SS' for display.
    public void convert() {
        current = SystemClock.elapsedRealtime() - timer.getBase();
        int seconds = (int)(current / 1000) % 60;
        int minutes = (int)(current / 1000) / 60;
        timeDisplay = String.format("%02d:%02d", minutes, seconds);
    }

    // Saves the latest 'previousWorkout' message as a shared preference.
    public void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PW", previousWorkout);
        editor.commit();
    }

    // Loads in the shared preferences containing the previous workout message.
    public void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", MODE_PRIVATE);
        previousWorkout = sharedPreferences.getString("PW", "No previous workout.");
    }
}