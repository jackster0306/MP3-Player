package com.psyjg14.coursework1.viewmodel;

import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

/**
 * ViewModel for the MusicListActivity.
 */
public class MusicListViewModel extends ViewModel {

    // LiveData for observing and updating the background color
    private final MutableLiveData<Integer> backgroundColour = new MutableLiveData<>();

    // LiveData for observing and updating the playback speed
    private final MutableLiveData<String> playbackSpeed = new MutableLiveData<>();

    // Flag indicating whether the service is bound
    private boolean isBound = false;

    /**
     * Constructor: Initializes default values for playback speed and background color.
     */
    public MusicListViewModel() {
        playbackSpeed.setValue("1"); // Default playback speed
        backgroundColour.setValue(Color.WHITE); // Default background color
    }

    /**
     * Getter for the background color LiveData.
     *
     * @return MutableLiveData<Integer> representing the background color.
     */
    public MutableLiveData<Integer> getBackgroundColour() {
        return backgroundColour;
    }

    /**
     * Setter for the background color LiveData.
     *
     * @param backgroundColour The new background color to be set.
     */
    public void setBackgroundColour(int backgroundColour) {
        this.backgroundColour.setValue(backgroundColour);
    }

    /**
     * Getter for the background color as an integer.
     *
     * @return int representing the background color.
     */
    public int getBackgroundColourAsInt() {
        return Objects.requireNonNull(backgroundColour.getValue());
    }

    /**
     * Setter for the playback speed LiveData.
     *
     * @param playbackSpeed The new playback speed to be set.
     */
    public void setPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed.setValue(String.valueOf(playbackSpeed));
    }

    /**
     * Getter for the flag indicating whether the service is bound.
     *
     * @return boolean representing whether the service is bound.
     */
    public boolean getIsBound() {
        return isBound;
    }

    /**
     * Setter for the flag indicating whether the service is bound.
     *
     * @param isBound The new value for the isBound flag.
     */
    public void setIsBound(boolean isBound) {
        this.isBound = isBound;
    }

    /**
     * Getter for the playback speed LiveData.
     *
     * @return MutableLiveData<String> representing the playback speed.
     */
    public MutableLiveData<String> getPlaybackSpeed() {
        return playbackSpeed;
    }

    /**
     * Getter for the playback speed as a float.
     *
     * @return float representing the playback speed.
     */
    public float getPlaybackSpeedAsFloat() {
        return Float.parseFloat(Objects.requireNonNull(playbackSpeed.getValue()));
    }
}
