package com.jondal.clock;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

/**
 * Monitors the text of the EditTexts
 */

public class EditTextWatcher implements TextWatcher {

    // Log Tag for this activity
    private static final String LOG_TAG = EditTextWatcher.class.getName();

    private Button startButton;
    private Button resetButton;
    private Button clearButton;

    public EditTextWatcher(Button startButton, Button resetButton, Button clearButton) {
        this.startButton = startButton;
        this.resetButton = resetButton;
        this.clearButton = clearButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        if (after == 0) {
            resetButtonState();
        } else {
            changeButtonState(ButtonEvent.TEXT);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    // enables and disables buttons
    private  void changeButtonState(ButtonEvent event) {

        switch (event) {
            case START:
                startButton.setEnabled(false);
                clearButton.setEnabled(false);
                resetButton.setEnabled(true);
                break;
            case TEXT:
                startButton.setEnabled(true);
            case RESET:
                resetButton.setEnabled(false);
                clearButton.setEnabled(true);
                break;
            case CLEAR:
                resetButtonState();
                break;
            default: Log.e(LOG_TAG, "unexpected event: " + event);
        }
    }

    // resets the buttons to their original state
    private void resetButtonState() {
        startButton.setEnabled(false);
        resetButton.setEnabled(false);
        clearButton.setEnabled(false);
    }
}
