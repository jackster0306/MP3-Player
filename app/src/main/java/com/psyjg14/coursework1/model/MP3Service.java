package com.psyjg14.coursework1.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.psyjg14.coursework1.R;

/**
 * Background service responsible for handling MP3 audio playback.
 */
public class MP3Service extends Service {

    // Notification channel ID
    private static final String CHANNEL_ID = "PlayingChannel";

    // Progress of the playback
    private int progress = 0;

    // Notification ID
    private static final int NOTIFICATION_ID = 1;

    // Log tag for debugging
    private static final String TAG = "COMP3018 - CW1";

    // Flags for the service state
    private boolean stopPlaying = false;
    private boolean isPlaying = false;
    private boolean notificationActive = false;

    // Binder for other classes to interact with the service
    private final IBinder binder = new LocalBinder();

    // URI of the chosen MP3 file
    private String uri;

    // MP3Player instance for handling audio playback
    private MP3Player player;

    // Callback for progress updates during playback
    private PlayerCallback callback;

    // Notification object
    private Notification notification;

    // Playback speed of the audio
    private static float playbackSpeed;

    // Last progress to check if playback is finished
    private double lastProgress = 0;

    /**
     * Default constructor.
     */
    public MP3Service() {
    }

    /**
     * Called when the service is created.
     */
    @Override
    public void onCreate() {
        Log.d(TAG, "Service creation initiated.");
        super.onCreate();
        player = new MP3Player();
        createNotificationChannel();
    }

    /**
     * Called when the service is started.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Begin playing the chosen song
        Log.d(TAG, "Service start command received.");

        // Check if the song is already playing
        if (isPlaying) {
            Log.d(TAG, "Song is already in progress. Ignoring request.");
            return START_NOT_STICKY;
        }

        // Build the notification
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Player Service")
                .setContentText("Playing selected music")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        Log.d(TAG, "Starting service in foreground.");

        //Start Notification
        startForeground(NOTIFICATION_ID, notification);
        notificationActive = true;

        // Thread for playing music
        new Thread(() -> {
            isPlaying = true;
            Log.d(TAG, "Audio playback task initiated.");

            //Start playing music
            player.load(uri, playbackSpeed);
            int duration = player.getDuration();

            // Loop until value of stopPlaying is set to true
            while (!stopPlaying) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Check if song is currently playing, or has been paused
                if (!isPlaying) {
                    // Stop notification if song is paused
                    stopForeground(true);
                } else {
                    if (!notificationActive) {
                        Log.d(TAG, "Reactivating notification.");
                        // Rebuild notification when play is pressed after a pause
                        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setContentTitle("Player Service")
                                .setContentText("Playing selected music")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .build();
                        startForeground(NOTIFICATION_ID, notification);
                        notificationActive = true;
                    }
                    if (callback != null) {
                        // Update progress and check for playback stuck
                        progress = player.getProgress();
                        double percent = ((double) progress / duration) * 100;
                        //Check if song has finished
                        if (percent == lastProgress) {
                            callback.onPlayingProgress(0);
                            stopPlaying = true;
                            isPlaying = false;
                            player.stop();
                        } else {
                            lastProgress = percent;
                            callback.onPlayingProgress((int) percent);
                        }
                    }
                }
            }
            Log.d(TAG, "Audio playback task completed.");
            Log.d(TAG, "Stopping service and foreground status.");

            // Stop the service and notification
            stopSelf();
            stopForeground(true);
            stopPlaying = false;
            isPlaying = false;
        }).start();
        Log.d(TAG, "Stopping player.");
        return START_STICKY;
    }

    /**
     * Create notification channel.
     */
    private void createNotificationChannel() {
        Log.d(TAG, "Creating notification channel.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Player Service";
            String description = "Used for playing music";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel creation complete.");
        }
    }

    /**
     * Called when the service is destroyed.
     */
    @Override
    public void onDestroy() {
        // Stop the audio playback
        Log.d(TAG, "Service destruction initiated.");
        player.stop();
        if (callback != null) {
            callback.onPlayingProgress(0);
        }
        super.onDestroy();
    }

    /**
     * Method to stop the audio playback.
     */
    public void stopSong() {
        stopPlaying = true;
    }

    /**
     * Method to pause the audio playback.
     */
    public void pauseSong() {
        player.pause();
        isPlaying = false;
        notificationActive = false;
    }

    /**
     * Method to resume audio playback.
     */
    public void playSong() {
        if (!isPlaying && !stopPlaying) {
            player.play();
            isPlaying = true;
        }
    }

    /**
     * Binder class for other classes to interact with the service.
     */
    public class LocalBinder extends Binder {
        public MP3Service getService() {
            return MP3Service.this;
        }
    }

    /**
     * Called when a client binds to the service.
     *
     * @return The binder interface.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Set the URI of the chosen MP3 file.
     *
     * @param uri The URI of the MP3 file.
     */
    public void setSong(String uri) {
        this.uri = uri;
    }

    /**
     * Interface for receiving playback progress updates.
     */
    public interface PlayerCallback {
        void onPlayingProgress(int progress);
    }

    /**
     * Set the callback for progress updates.
     *
     * @param callback The callback to be set.
     */
    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

    /**
     * Set the playback speed for the audio.
     *
     * @param f The playback speed.
     */
    public void setPlaybackSpeed(float f) {
        playbackSpeed = f;
        if(player != null){
            player.setPlaybackSpeed(f);
        }
    }

    /**
     * Called when the task is removed from the recent tasks list.
     *
     * @param rootIntent The intent that was used to launch the root activity.
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Task removed from recent tasks list.");
        stopSelf();
        stopForeground(true);
        super.onTaskRemoved(rootIntent);
    }
}