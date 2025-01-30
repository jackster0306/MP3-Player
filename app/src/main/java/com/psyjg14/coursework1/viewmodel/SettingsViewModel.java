package com.psyjg14.coursework1.viewmodel;

import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

/**
 * ViewModel for the SettingsActivity.
 */
public class SettingsViewModel extends ViewModel {

    // LiveData for observing and updating the background color
    private final MutableLiveData<Integer> backgroundColour = new MutableLiveData<>();

    // LiveData for observing and updating the playback speed
    private final MutableLiveData<String> playbackSpeed = new MutableLiveData<>();

    // LiveData for observing and updating the checkbox state for the white background color
    private final MutableLiveData<Boolean> isWhiteChecked = new MutableLiveData<>();

    // LiveData for observing and updating the checkbox state for the blue background color
    private final MutableLiveData<Boolean> isBlueChecked = new MutableLiveData<>();

    // LiveData for observing and updating the checkbox state for the red background color
    private final MutableLiveData<Boolean> isRedChecked = new MutableLiveData<>();

    // Flag indicating whether the service is bound
    private boolean isBound = false;

    // Flag indicating whether the playback speed has changed
    private boolean hasPlaybackSpeedChanged = false;

    // Initial playback speed value
    private float initialPlaybackSpeed = 1;

    /**
     * Constructor: Initializes default values for playback speed, background color, and checkbox states.
     */
    public SettingsViewModel() {
        playbackSpeed.setValue("1"); // Default playback speed
        backgroundColour.setValue(Color.WHITE); // Default background color
        isWhiteChecked.setValue(true); // Default checkbox state for white background color
        isBlueChecked.setValue(false); // Default checkbox state for blue background color
        isRedChecked.setValue(false); // Default checkbox state for red background color
    }

    /**
     * Getter for the checkbox state of the white background color.
     *
     * @return MutableLiveData<Boolean> representing the checkbox state.
     */
    public MutableLiveData<Boolean> getIsWhiteChecked() {
        return isWhiteChecked;
    }

    /**
     * Getter for the checkbox state of the blue background color.
     *
     * @return MutableLiveData<Boolean> representing the checkbox state.
     */
    public MutableLiveData<Boolean> getIsBlueChecked() {
        return isBlueChecked;
    }

    /**
     * Getter for the checkbox state of the red background color.
     *
     * @return MutableLiveData<Boolean> representing the checkbox state.
     */
    public MutableLiveData<Boolean> getIsRedChecked() {
        return isRedChecked;
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
        checkClicked(backgroundColour); // Update checkbox states based on the selected background color
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
     * Getter for the playback speed LiveData.
     *
     * @return MutableLiveData<String> representing the playback speed.
     */
    public MutableLiveData<String> getPlaybackSpeed() {
        if (playbackSpeed.getValue() == null || playbackSpeed.getValue().isEmpty()) {
            playbackSpeed.setValue(null);
            return playbackSpeed;
        } else {
            if (Float.parseFloat(playbackSpeed.getValue()) == initialPlaybackSpeed) {
                hasPlaybackSpeedChanged = false;
            } else {
                hasPlaybackSpeedChanged = true;
            }
            return playbackSpeed;
        }
    }

    /**
     * Setter for the playback speed LiveData.
     *
     * @param playbackSpeed The new playback speed to be set.
     */
    public void setPlaybackSpeed(String playbackSpeed) {
        if (Float.parseFloat(playbackSpeed) == initialPlaybackSpeed) {
            hasPlaybackSpeedChanged = false;
        } else {
            hasPlaybackSpeedChanged = true;
        }
        this.playbackSpeed.setValue(playbackSpeed);
    }

    /**
     * Getter for the playback speed as a float.
     *
     * @return float representing the playback speed.
     */
    public float getPlaybackSpeedAsFloat() {
        return Float.parseFloat(Objects.requireNonNull(playbackSpeed.getValue()));
    }

    /**
     * Handler for the white background color click event.
     * Updates the background color and checkbox states.
     */
    public void onWhiteClicked() {
        setBackgroundColour(Color.WHITE);
    }

    /**
     * Handler for the blue background color click event.
     * Updates the background color and checkbox states.
     */
    public void onBlueClicked() {
        setBackgroundColour(Color.BLUE);
    }

    /**
     * Handler for the red background color click event.
     * Updates the background color and checkbox states.
     */
    public void onRedClicked() {
        setBackgroundColour(Color.RED);
    }

    /**
     * Validates whether the playback speed is within a valid range.
     *
     * @return boolean indicating whether the playback speed is valid.
     */
    public boolean isPlaybackSpeedValid() {
        if (playbackSpeed.getValue() != null && !playbackSpeed.getValue().isEmpty() &&
                Float.parseFloat(playbackSpeed.getValue()) > 0 && Float.parseFloat(playbackSpeed.getValue()) < 5) {
            return true;
        } else {
            playbackSpeed.setValue("1");
            initialPlaybackSpeed = 1;
            return false;
        }
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
     * Getter for the flag indicating whether the playback speed has changed.
     *
     * @return boolean representing whether the playback speed has changed.
     */
    public boolean getIsSaved() {
        return !hasPlaybackSpeedChanged;
    }

    /**
     * Setter for the flag indicating whether the playback speed has changed.
     * Resets the flag and updates the initial playback speed.
     */
    public void setIsSaved() {
        hasPlaybackSpeedChanged = false;
        initialPlaybackSpeed = Float.parseFloat(playbackSpeed.getValue());
    }

    /**
     * Updates the checkbox states based on the selected background color.
     *
     * @param backgroundColour The selected background color.
     */
    public void checkClicked(int backgroundColour) {
        if (backgroundColour == Color.WHITE) {
            isWhiteChecked.setValue(true);
            isBlueChecked.setValue(false);
            isRedChecked.setValue(false);
        } else if (backgroundColour == Color.BLUE) {
            isWhiteChecked.setValue(false);
            isBlueChecked.setValue(true);
            isRedChecked.setValue(false);
        } else if (backgroundColour == Color.RED) {
            isWhiteChecked.setValue(false);
            isBlueChecked.setValue(false);
            isRedChecked.setValue(true);
        }
    }

    /**
     * Setter for the initial playback speed.
     *
     * @param initialPlaybackSpeed The new initial playback speed.
     */
    public void setInitialPlaybackSpeed(float initialPlaybackSpeed) {
        this.initialPlaybackSpeed = initialPlaybackSpeed;
    }
}
