package com.jondal.clock.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jondal.clock.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clock
 */

public class ClockFragment extends Fragment {

    private final String CLOCK_24 = "HH:mm:ss";
    private final String CLOCK_12 = "h:mm:ss a";
    private final String SWITCH_24 = "Switch to 24-hour clock";
    private final String SWITCH_12 = "Switch to 12-hour clock";

    private String currentFormat;

    private View rootView;
    private TextView timeView;
    private TextView dateView;
    private Timer timeTimer;
    private Timer dateTimer;
    private Button switchButton;

    public ClockFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_clock, container, false);

        switchButton = rootView.findViewById(R.id.switch_format);
        currentFormat = CLOCK_24;

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFormat();
            }
        });

        return rootView;
    }

    // begins task to update time and date every second when activity is resumed
    @Override
    public void onResume() {
        super.onResume();

        timeTimer = new Timer(true);
        timeTimer.scheduleAtFixedRate(new UpdateTime(), 0, 1000);

        dateTimer = new Timer(true);
        dateTimer.scheduleAtFixedRate(new UpdateDate(), 0, 1000);
    }

    private void switchFormat() {

        switch (currentFormat) {
            case CLOCK_24:
                currentFormat = CLOCK_12;
                switchButton.setText(SWITCH_24);
                break;
            case CLOCK_12:
                currentFormat = CLOCK_24;
                switchButton.setText(SWITCH_12);
                break;
                default: currentFormat = CLOCK_24;
        }
    }

    // terminates the timers when the user leaves the fragment
    @Override
    public void onPause() {
        super.onPause();

        if (timeTimer != null) {
            timeTimer.cancel();
        }
        if (dateTimer != null) {
            dateTimer.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (timeTimer != null) {
            timeTimer.purge();
        }
        if (dateTimer != null) {
            dateTimer.purge();
        }
    }

    // TimerTask to handle updating the Time
    private class UpdateTime extends TimerTask {

        public void run() {
            // updates the TextView on the main UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timeView = rootView.findViewById(R.id.time);
                    timeView.setText(getTime());
                }
            });
        }

        // returns a Date object formatted in HOUR:MINUTE:SECOND
        private String getTime() {
            DateFormat df = new SimpleDateFormat(currentFormat, Locale.US);
            Date date = new Date();
            return df.format(date);
        }
    }

    // TimerTask to handle updating the Date
    private class UpdateDate extends TimerTask {
        public void run() {
            // updates the TextView on the main UI thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dateView = rootView.findViewById(R.id.date);
                    dateView.setText(getDate());
                }
            });
        }

        // returns a Date object formatted in DAY, MONTH DATE
        private String getDate() {


            DateFormat df = new SimpleDateFormat("EEEE, MMMM dd", Locale.US);
            Date date = new Date();
            return df.format(date);
        }
    }


}
