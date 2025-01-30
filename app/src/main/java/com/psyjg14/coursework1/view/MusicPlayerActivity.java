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
import com.psyjg14.coursework1.databinding.ActivityMusicPlayerBinding;
import com.psyjg14.coursework1.model.MP3Service;
import com.psyjg14.coursework1.viewmodel.MusicPlayerViewModel;

/**
 * Activity for controlling and displaying the music player interface.
 */
public class MusicPlayerActivity extends AppCompatActivity {

    // Log tag for debugging
    private static final String TAG = "COMP3018 - CW1";

    // Reference to the MP3Service
    private MP3Service mp3Service;

    // ViewModel for managing data related to the music player
    MusicPlayerViewModel musicPlayerViewModel;

    /**
     * Lifecycle method for initializing the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding initialization
        ActivityMusicPlayerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);
        musicPlayerViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
        binding.setViewModel(musicPlayerViewModel);
        binding.setLifecycleOwner(this);

        // Retrieve background color and playback speed from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("colour") && intent.hasExtra("speed")) {
            musicPlayerViewModel.setBackgroundColour(intent.getIntExtra("colour", Color.WHITE));
            musicPlayerViewModel.setPlaybackSpeed(intent.getFloatExtra("speed", 1));
        }

        // Observer for background color changes
        binding.getViewModel().getBackgroundColour().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                // Update the UI with the new background color
                binding.mpLayout.setBackgroundColor(color);
            }
        });

        // OnBackPressedCallback for handling back button presses or user swiping back, depending on the device
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onMusicListClicked(null);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Click handler for the play button. Plays the song if it is paused, otherwise does nothing.
     */
    public void onPlayClicked(View v) {
        mp3Service.playSong();
    }

    /**
     * Click handler for the pause button. Pauses the song if it is playing, otherwise does nothing.
     */
    public void onPauseClicked(View v) {
        mp3Service.pauseSong();
    }

    /**
     * Click handler for the stop button. Stops the song if it is playing, otherwise does nothing.
     */
    public void onStopClicked(View v) {
        Log.d(TAG, "stopService Intent Launched");
        Log.d(TAG, "Changing value of cancelDownload in the service");
        mp3Service.stopSong();

        // Stop the service
        Intent intent = new Intent(MusicPlayerActivity.this, MP3Service.class);
        stopService(intent);
        // Unbind the service
        if (musicPlayerViewModel.getIsBound()) {
            unbindService(connection);
            musicPlayerViewModel.setIsBound(false);
        }
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
            musicPlayerViewModel.setIsBound(true);

            mp3Service.setCallback(progress -> runOnUiThread(() -> musicPlayerViewModel.setProgress(progress)));
        }

        // Called when a client unbinds from the service
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicPlayerViewModel.setIsBound(false);
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
        if (musicPlayerViewModel.getIsBound()) {
            unbindService(connection);
            musicPlayerViewModel.setIsBound(false);
        }
    }

    /**
     * Click handler for returning to the music list activity.
     */
    public void onMusicListClicked(View v) {
        // Passes the current background color and playback speed to the Music List activity
        Intent intent = new Intent();
        intent.putExtra("colour", musicPlayerViewModel.getBackgroundColourAsInt());
        intent.putExtra("speed", musicPlayerViewModel.getPlaybackSpeedAsFloat());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Saves the current background color and playback speed
        super.onSaveInstanceState(outState);
        outState.putInt("colour", musicPlayerViewModel.getBackgroundColourAsInt());
        outState.putFloat("speed", musicPlayerViewModel.getPlaybackSpeedAsFloat());
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restores the background color and playback speed
        super.onRestoreInstanceState(savedInstanceState);
        musicPlayerViewModel.setBackgroundColour(savedInstanceState.getInt("colour"));
        musicPlayerViewModel.setPlaybackSpeed(savedInstanceState.getFloat("speed"));
    }
}
