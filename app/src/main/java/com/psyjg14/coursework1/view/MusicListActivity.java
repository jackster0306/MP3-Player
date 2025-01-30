package com.psyjg14.coursework1.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.psyjg14.coursework1.R;
import com.psyjg14.coursework1.databinding.ActivityMusicListBinding;
import com.psyjg14.coursework1.model.MP3Service;
import com.psyjg14.coursework1.viewmodel.MusicListViewModel;

import java.io.File;

/**
 * Activity for displaying a list of MP3 files for the user to choose from and navigating to Settings or Music Player.
 */
public class MusicListActivity extends AppCompatActivity {

    // Log tag for debugging
    private static final String TAG = "COMP3018 - CW1";

    // Activity result launcher for handling activity results
    ActivityResultLauncher<Intent> activityResultLauncher;

    // ViewModel for managing data related to the music list
    MusicListViewModel musicListViewModel;

    // Reference to the MP3Service
    private MP3Service mp3Service;

    /**
     * Lifecycle method for initializing the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data binding initialization
        ActivityMusicListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_music_list);
        musicListViewModel = new ViewModelProvider(this).get(MusicListViewModel.class);
        binding.setViewModel(musicListViewModel);
        binding.setLifecycleOwner(this);

        // Activity result launcher initialization
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("colour") && data.hasExtra("speed")) {
                            musicListViewModel.setBackgroundColour(data.getIntExtra("colour", Color.WHITE));
                            musicListViewModel.setPlaybackSpeed(data.getFloatExtra("speed", 1));
                        }
                    }
                }
        );

        // Observer for background color changes
        binding.getViewModel().getBackgroundColour().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                // Update the UI with the new background color
                binding.mainLayout.setBackgroundColor(color);
            }
        });

        // ListView initialization and setup. Gets the song files from the SD Card and displays them.
        final ListView lv = binding.musicListView;
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0", null,
                null);
        lv.setAdapter(new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor,
                new String[]{MediaStore.Audio.Media.DATA}, new
                int[]{android.R.id.text1}) {
            @Override
            public void setViewText(TextView v, String filePath) {
                File file = new File(filePath);
                String fileName = file.getName();

                // Removes .mp3 extension
                v.setText(fileName.substring(0, fileName.length() - 4));
            }
        });

        // Item click listener for starting audio playback. When item is clicked, music starts playing
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                Cursor c = (Cursor) lv.getItemAtPosition(myItemInt);
                @SuppressLint("Range") String uri =
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                Log.d(TAG, "StartService Intent Launched");
                if (!musicListViewModel.getIsBound()) {
                    //Binds to the service
                    Intent intent = new Intent(MusicListActivity.this, MP3Service.class);
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);
                }

                // Sets the song and playback speed and starts the service
                mp3Service.setSong(uri);
                mp3Service.setPlaybackSpeed(musicListViewModel.getPlaybackSpeedAsFloat());
                Intent intent = new Intent(MusicListActivity.this, MP3Service.class);
                startService(intent);
            }
        });
    }

    /**
     * Service connection for interacting with the MP3Service.
     */
    private ServiceConnection connection = new ServiceConnection() {

        // Called when the service is connected
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            MP3Service.LocalBinder binder = (MP3Service.LocalBinder) service;
            mp3Service = binder.getService();
            musicListViewModel.setIsBound(true);
        }

        // Called when the service is disconnected
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicListViewModel.setIsBound(false);
        }
    };

    /**
     * Called when the activity is started.
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart called");
        super.onStart();

        // Binds to the service
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

        // Unbinds from the service
        if (musicListViewModel.getIsBound()) {
            unbindService(connection);
            musicListViewModel.setIsBound(false);
        }
    }

    /**
     * Click handler for opening the settings activity.
     */
    public void onSettingsClicked(View v) {
        // Passes the current background color and playback speed to the settings activity
        Intent intent = new Intent(MusicListActivity.this, SettingsActivity.class);
        intent.putExtra("colour", musicListViewModel.getBackgroundColourAsInt());
        intent.putExtra("speed", musicListViewModel.getPlaybackSpeedAsFloat());
        activityResultLauncher.launch(intent);
    }

    /**
     * Click handler for opening the music player activity.
     */
    public void onMusicPlayerClicked(View v) {
        // Passes the current background color and playback speed to the music player activity
        Intent intent = new Intent(MusicListActivity.this, MusicPlayerActivity.class);
        intent.putExtra("colour", musicListViewModel.getBackgroundColourAsInt());
        intent.putExtra("speed", musicListViewModel.getPlaybackSpeedAsFloat());
        activityResultLauncher.launch(intent);
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saves the current background color and playback speed
        outState.putInt("colour", musicListViewModel.getBackgroundColourAsInt());
        outState.putFloat("speed", musicListViewModel.getPlaybackSpeedAsFloat());
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restores the background color and playback speed
        musicListViewModel.setBackgroundColour(savedInstanceState.getInt("colour"));
        musicListViewModel.setPlaybackSpeed(savedInstanceState.getFloat("speed"));
    }
}
