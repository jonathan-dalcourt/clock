package com.jondal.clock.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jondal.clock.ButtonEvent;
import com.jondal.clock.EditTextWatcher;
import com.jondal.clock.MinMaxFilter;
import com.jondal.clock.R;

/**
 * Timer
 */

public class TimerFragment extends Fragment {

    // Log Tag for this activity
    private static final String LOG_TAG = TimerFragment.class.getName();

    //
    private static final String ZERO_TIME = "0";
    private static final String DOUBLE_ZERO_TIME = "00";

    /** Class Constants for readability */

    // Constants for the array of Time values
    private static long[] time;
    private static final int HOURS = 0;
    private static final int MINUTES = 1;
    private static final int SECONDS = 2;
    private static final int TOTAL_SECONDS = 3;
    private static final int TIME_LENGTH = 4;

    // constants for unit conversions
    private static final int HOU_SEC = 3600;
    private static final int MIN_SEC = 60;
    private static final int SEC_MSEC = 1000;
    private static final int MIN_MSEC = MIN_SEC * SEC_MSEC;
    private static final int HOU_MSEC = HOU_SEC * SEC_MSEC;

    private EditText editHours;
    private EditText editMinutes;
    private EditText editSeconds;

    private TextView textHours;
    private TextView textMinutes;
    private TextView textSeconds;

    private Button startButton;
    private Button resetButton;
    private Button clearButton;

    private SimpleCountdownTimer timer;

    public TimerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        editHours = rootView.findViewById(R.id.hours_edit);
        editMinutes = rootView.findViewById(R.id.minutes_edit);
        editSeconds = rootView.findViewById(R.id.seconds_edit);

        startButton = rootView.findViewById(R.id.start);
        resetButton = rootView.findViewById(R.id.reset);
        clearButton = rootView.findViewById(R.id.clear);

        InputFilter[] filter = new InputFilter[]{ new MinMaxFilter()};
        editHours.setFilters(filter);
        editMinutes.setFilters(filter);
        editSeconds.setFilters(filter);

        EditTextWatcher watcher = new EditTextWatcher(startButton, resetButton, clearButton);
        editHours.addTextChangedListener(watcher);
        editMinutes.addTextChangedListener(watcher);
        editSeconds.addTextChangedListener(watcher);

        textHours = rootView.findViewById(R.id.hours_timer);
        textMinutes = rootView.findViewById(R.id.minutes_timer);
        textSeconds = rootView.findViewById(R.id.seconds_timer);

        time = new long[TIME_LENGTH];

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonState(ButtonEvent.START);
                startTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonState(ButtonEvent.RESET);
                resetTimer();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonState(ButtonEvent.CLEAR);
                clearTimer();
            }
        });

        return rootView;
    }

    // initialises the buttons after the application is resumed
    @Override
    public void onResume() {
        super.onResume();
        initButtonState();
    }

    //
    private void initButtonState() {
        if (!getEmpty()) {
            clearButton.setEnabled(true);
            startButton.setEnabled(true);
        } else {
            clearButton.setEnabled(false);
            startButton.setEnabled(false);
        }
        resetButton.setEnabled(false);
    }

    private boolean getEmpty() {
        return (editHours.getText().toString().equals("") && editMinutes.getText().toString().equals("")
                && editSeconds.getText().toString().equals(""));
    }

    // enables and disables buttons
    public void changeButtonState(ButtonEvent event) {

        switch (event) {
            case START:
                startButton.setEnabled(false);
                clearButton.setEnabled(false);
                resetButton.setEnabled(true);
                break;
            case TEXT:
                startButton.setEnabled(true);
            case RESET:
                startButton.setEnabled(true);
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
    public void resetButtonState() {
        startButton.setEnabled(false);
        resetButton.setEnabled(false);
        clearButton.setEnabled(false);
    }


    // begins the timer
    private void startTimer() {

        initTimeArray();

        long totalMilliseconds = time[TOTAL_SECONDS] * SEC_MSEC;


        if (totalMilliseconds == 0) {
            try {
                Toast.makeText(getActivity(), R.string.timer_empty, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "NullPointerException: ", e);
            }
        } else {
            timer = new SimpleCountdownTimer(totalMilliseconds, SEC_MSEC);
            timer.start();
        }

    }

    // initializes the array of Time values from the EditTexts
    private void initTimeArray() {
        // gets the values of the EditTexts
        String stringHours = editHours.getText().toString().trim();
        String stringMinutes = editMinutes.getText().toString().trim();
        String stringSeconds = editSeconds.getText().toString().trim();

        // sets the indices of the array to the values of the EditTexts
        time[HOURS] = (stringHours.equals("") ? 0 : Integer.parseInt(stringHours));
        time[MINUTES] = (stringMinutes.equals("") ? 0 : Integer.parseInt(stringMinutes));
        time[SECONDS] = (stringSeconds.equals("") ? 0 : Integer.parseInt(stringSeconds));

        // converts all values to seconds and adds them together
        long secondsTotal = time[SECONDS] + (time[MINUTES] * MIN_SEC) + (time[HOURS] * HOU_SEC);
        time[TOTAL_SECONDS] = secondsTotal;
    }

    // sets the TextViews that make up the timer to the passed H/M/S values
    private void setTimeView(String hours, String minutes, String seconds) {

        hours = (Integer.parseInt(hours) < 10 ? "0" + hours : "" + hours);
        minutes = (Integer.parseInt(minutes) < 10 ? "0" + minutes : "" + minutes);
        seconds = (Integer.parseInt(seconds) < 10 ? "0" + seconds : "" + seconds);

        textHours.setText(hours);
        textMinutes.setText(minutes);
        textSeconds.setText(seconds);

    }

    // resets the TextViews that make up the timer
    private void resetTimer() {
        initTimeArray();

        if (timer != null) {
            timer.cancel();
        }
        String hours = Long.toString(time[HOURS]);
        String minutes = Long.toString(time[MINUTES]);
        String seconds = Long.toString(time[SECONDS]);

        setTimeView(hours, minutes, seconds);
    }

    private void clearTimer() {
        resetTimer();

        setTimeView(ZERO_TIME, ZERO_TIME, ZERO_TIME);

        editHours.setText("");
        editMinutes.setText("");
        editSeconds.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    // custom Countdown Timer
    private class SimpleCountdownTimer extends CountDownTimer {

        // constructor for the custom CountdownTimer
        public SimpleCountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // updates the Timer TextViews every second
        public void onTick(long millisUntilFinished) {

            String hours = Integer.toString((int) ((millisUntilFinished / HOU_MSEC) % MIN_SEC));
            String minutes = Integer.toString((int) ((millisUntilFinished / MIN_MSEC) % MIN_SEC));
            String seconds = Integer.toString((int) ((millisUntilFinished / SEC_MSEC) % MIN_SEC));

           setTimeView(hours, minutes, seconds);
        }

        // sends a Toast and sets the Timer TextViews to "00"
        public void onFinish() {
            setTimeView(ZERO_TIME, ZERO_TIME, ZERO_TIME);
            resetButtonState();
            try {
                Toast.makeText(getActivity(), R.string.timer_done, Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "NullPointerException: ", e);
            }
        }
    }
}
