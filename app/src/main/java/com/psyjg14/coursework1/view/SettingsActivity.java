package com.psyjg14.coursework1.view;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.psyjg14.coursework1.R;
import com.psyjg14.coursework1.databinding.ActivitySettingsBinding;
import com.psyjg14.coursework1.model.MP3Service;
import com.psyjg14.coursework1.viewmodel.SettingsViewModel;

/**
 * SettingsActivity: Manages and displays user settings.
 */
public class SettingsActivity extends AppCompatActivity {

    // Log tag for debugging
    private static final String TAG = "COMP3018 - CW1";

    // Reference to the MP3Service
    private MP3Service mp3Service;

    // ViewModel for managing data related to settings activity
    SettingsViewModel settingsViewModel;

    /**
     * Lifecycle method for initializing the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding initialization
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setViewModel(settingsViewModel);
        binding.setLifecycleOwner(this);

        // Retrieve initial values from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("colour") && intent.hasExtra("speed")) {
            settingsViewModel.setBackgroundColour(intent.getIntExtra("colour", Color.WHITE));
            settingsViewModel.setPlaybackSpeed(String.valueOf(intent.getFloatExtra("speed", 1)));
            settingsViewModel.setInitialPlaybackSpeed(intent.getFloatExtra("speed", 1));
        }

        // OnBackPressedCallback for handling back button presses or user swiping back, depending on the device
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackClicked(null);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Observer for background color changes
        binding.getViewModel().getBackgroundColour().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                // Update the UI with the new background color
                binding.settingsLayout.setBackgroundColor(color);
            }
        });
    }

    /**
     * Click handler for the back button. If the playback speed is valid and settings are saved, return to the previous activity.
     */
    public void onBackClicked(View v) {
        if (settingsViewModel.isPlaybackSpeedValid() && settingsViewModel.getIsSaved()) {
            // Passes the current background color and playback speed to the Music List activity
            Intent intent = new Intent();
            intent.putExtra("colour", settingsViewModel.getBackgroundColourAsInt());
            intent.putExtra("speed", settingsViewModel.getPlaybackSpeedAsFloat());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    /**
     * Click handler for the save button. If the playback speed is valid, update the MP3Service and mark settings as saved.
     */
    public void onSaveClicked(View v) {
        if (settingsViewModel.isPlaybackSpeedValid()) {
            mp3Service.setPlaybackSpeed(settingsViewModel.getPlaybackSpeedAsFloat());
        }
        settingsViewModel.setIsSaved();
    }

    /**
     * Service connection for interacting with the MP3Service.
     */
    private ServiceConnection connection = new ServiceConnection() {

        // Called when a client binds to the service
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            MP3Service.LocalBinder binder = (MP3Service.LocalBinder) service;
            mp3Service = binder.getService();
            Log.d(TAG, "isBound set to true");
            settingsViewModel.setIsBound(true);
        }

        // Called when the service is disconnected
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            settingsViewModel.setIsBound(false);
        }
    };

    /**
     * Called when the activity is started.
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();
        Intent intent = new Intent(this, MP3Service.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when the activity is stopped.
     */
    @Override
    protected void onStop() {
        Log.d(TAG, "OnStop called");
        super.onStop();
        if (settingsViewModel.getIsBound()) {
            unbindService(connection);
            settingsViewModel.setIsBound(false);
        }
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the current background color, playback speed
        super.onSaveInstanceState(outState);
        outState.putInt("colour", settingsViewModel.getBackgroundColourAsInt());
        outState.putFloat("speed", settingsViewModel.getPlaybackSpeedAsFloat());
        outState.putBoolean("isSaved", settingsViewModel.getIsSaved());
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the background color, playback speed and isSaved flag
        super.onRestoreInstanceState(savedInstanceState);
        settingsViewModel.setBackgroundColour(savedInstanceState.getInt("colour"));
        settingsViewModel.setPlaybackSpeed(String.valueOf(savedInstanceState.getFloat("speed")));
        boolean isSaved = savedInstanceState.getBoolean("isSaved");
        if(isSaved){
            settingsViewModel.setIsSaved();
        }
    }
}