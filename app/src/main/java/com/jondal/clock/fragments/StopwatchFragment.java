package com.jondal.clock.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jondal.clock.ButtonEvent;
import com.jondal.clock.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Stopwatch
 */

public class StopwatchFragment extends Fragment {

    // Log Tag for this activity
    public static final String LOG_TAG = StopwatchFragment.class.getName();

    public static final String ZERO_TIME = "00";

    // constants for unit conversions
    private static final int HOU_SEC = 3600;
    private static final int MIN_SEC = 60;
    private static final int SEC_MSEC = 10;
    private static final int MIN_MSEC = MIN_SEC * SEC_MSEC;
    private static final int HOU_MSEC = HOU_SEC * SEC_MSEC;

    /** Objects used throughout Fragment */

    private TextView textMinutes;
    private TextView textSeconds;
    private TextView textMilliseconds;

    private LinearLayout lapHeaderLayout;
    private LinearLayout lapNumberLayout;
    private LinearLayout lapTimeLayout;
    private LinearLayout timeTotalLayout;

    private Button startButton;
    private Button pauseButton;
    private Button lapButton;
    private Button resetButton;

    private ScrollView lapsScrollView;

    private Timer stopwatch;

    // number of laps elapsed
    private static int lapCount;

    /** variables to keep track of the time counts */

    // current time counts
    private static int minCount;
    private static int secCount;
    private static int msCount;

    // current lap time counts
    private static int lapMinCount;
    private static int lapSecCount;
    private static int lapMSCount;

    // current combined count of minutes, seconds, and milliseconds in ms
    private static int currentCount;
    // previous total combined count
    private static int prevTotalCount;


    // constructor for the Stopwatch Fragment
    public StopwatchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        // initializes the count values
        lapCount = 1;
        minCount = 0;
        secCount = 0;
        msCount = 0;

        /** creates all the necessary views */
        lapsScrollView = rootView.findViewById(R.id.laps);

        lapHeaderLayout = rootView.findViewById(R.id.lap_headers);
        lapNumberLayout = rootView.findViewById(R.id.lap_number);
        lapTimeLayout = rootView.findViewById(R.id.time_lap);
        timeTotalLayout = rootView.findViewById(R.id.time_total);

        textMinutes = rootView.findViewById(R.id.minutes_timer);
        textSeconds = rootView.findViewById(R.id.seconds_timer);
        textMilliseconds = rootView.findViewById(R.id.milliseconds_timer);


        startButton = rootView.findViewById(R.id.start);
        pauseButton = rootView.findViewById(R.id.pause);
        lapButton = rootView.findViewById(R.id.lap);
        resetButton = rootView.findViewById(R.id.reset);

        // gets the elapsed time at the moment the user presses start and begins the stopwatch
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopwatch == null) {
                    changeButtonState(ButtonEvent.START);
                    if (!pauseButton.isEnabled()) {
                        prevTotalCount = getMSCount();
                    }
                    startStopwatch();
                }
            }
        });

        // pauses the stopwatch
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonState(ButtonEvent.PAUSE);
                pauseStopwatch();
                startButton.setEnabled(true);
            }
        });

        // laps the stopwatch
        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeButtonState(ButtonEvent.LAP);
                startLap();
                prevTotalCount = getMSCount();
            }
        });

        // resets all values
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonState(ButtonEvent.RESET);
                resetStopwatch();
            }
        });

        return rootView;
    }

    // enables and disables buttons
    private void changeButtonState(ButtonEvent event) {

        switch (event) {
            case START:
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                resetButton.setEnabled(true);
                lapButton.setEnabled(true);
                break;
            case RESET:
                resetButton.setEnabled(false);
            case PAUSE:
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                lapButton.setEnabled(false);
                break;
            default: Log.e(LOG_TAG, "unexpected event: " + event);
        }
    }

    // starts the StopwatchTask
    private void startStopwatch() {
        stopwatch = new Timer(true);
        stopwatch.scheduleAtFixedRate(new StopwatchTask(), 0, 100);
    }

    // gets the sum of all current count in MS
    private int getMSCount() {
        return (msCount + (secCount * SEC_MSEC) + (minCount * MIN_MSEC));
    }

    // adds information about the current lap to the Laps LinearLayout
    private void startLap() {
        if (stopwatch != null) {
            lapsScrollView.setVisibility(View.VISIBLE);
            lapHeaderLayout.setVisibility(View.VISIBLE);
            addCurrentLap();
            addLapTime();
            addTotalTime();
            scroll();
        }
    }

    /** Methods to add views to the Laps ScrollView */

    // displays the current lap number
    private void addCurrentLap() {

        TextView lapNumber = new TextView(getActivity());

        String currentLap = "Lap " + lapCount;

        lapNumber.setText(currentLap);
        lapNumber.setGravity(Gravity.START);
        lapCount++;
        lapNumberLayout.addView(lapNumber);
    }

    // displays the current lap time
    private void addLapTime() {

        TextView lapTime = new TextView(getActivity());
        setTimeViewsLap(lapTime);
        lapTimeLayout.addView(lapTime);
    }

    // sets the values of the TextViews for the current lap time
    private void setTimeViewsLap(TextView view) {

        getLapTime();

        String minutes = "00";
        String seconds = "00";
        String milliSeconds = "" + lapMSCount;

        minutes = (lapMinCount < 10 ? "0" + lapMinCount : "" + lapMinCount);
        seconds = (lapSecCount < 10 ? "0" + lapSecCount : "" + lapSecCount);

        String time = minutes + ":" + seconds + "." + milliSeconds;

        view.setText(time);
        view.setGravity(Gravity.CENTER);
    }

    // displays the total time
    private void addTotalTime() {

        TextView totalTime = new TextView(getActivity());
        setTimeViewsTotal(totalTime);
        timeTotalLayout.addView(totalTime);
    }

    // sets the values of the TextViews for the total stopwatch time
    private void setTimeViewsTotal(TextView view) {

        String minutes = (minCount < 10 ? "0" + minCount : "" + minCount);
        String seconds = (secCount < 10 ? "0" + secCount : "" + secCount);
        String milliSeconds = "" + msCount;

        String time = minutes + ":" + seconds + "." + milliSeconds;

        view.setText(time);
        view.setGravity(Gravity.END);
    }

    /***/

    // gets how much time has elapsed in the current lap
    private void getLapTime() {

        currentCount = getMSCount();

        int lapTime = currentCount - prevTotalCount;

        lapMinCount = lapTime / MIN_MSEC;
        lapTime -= lapMinCount * MIN_MSEC;

        lapSecCount = lapTime / SEC_MSEC;
        lapTime -= lapSecCount * SEC_MSEC;

        lapMSCount = lapTime % SEC_MSEC;
    }

    // scrolls to the bottom of the ScrollView
    private void scroll() {
        lapsScrollView.post(new Runnable() {
            @Override
            public void run() {
                lapsScrollView.smoothScrollTo(0, lapsScrollView.getBottom());
            }
        });
    }

    // pauses the stopwatch
    private void pauseStopwatch() {
        if (stopwatch != null) {
            stopwatch.cancel();
            stopwatch.purge();
        }
        stopwatch = null;
    }

    // resets the stopwatch
    private void resetStopwatch() {

        pauseStopwatch();
        resetLaps();

        lapCount = 1;
        minCount = 0;
        secCount = 0;
        msCount = 0;
        prevTotalCount = 0;
        currentCount = 0;

        textMinutes.setText(ZERO_TIME);
        textSeconds.setText(ZERO_TIME);
        textMilliseconds.setText("0");
    }

    // removes all lap information from the ScrollView
    private void resetLaps() {

        if (lapNumberLayout.getChildCount() > 0) {
            lapNumberLayout.removeAllViews();
        }
        if (lapTimeLayout.getChildCount() > 0) {
            lapTimeLayout.removeAllViews();
        }
        if (timeTotalLayout.getChildCount() > 0) {
            timeTotalLayout.removeAllViews();
        }

        lapsScrollView.setVisibility(View.INVISIBLE);
        lapHeaderLayout.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        pauseStopwatch();
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseStopwatch();
    }

    // custom TimerTask to act as a stopwatch
    private class StopwatchTask extends TimerTask {

        private int prevMin;
        private int prevSec;

        public void run() {
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msCount++;
                        keepTime();
                    }
                });
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "activity null ", e);
            }

        }

        // updates the Time Counts
        private void keepTime() {

            textMilliseconds.setText(Integer.toString(msCount));

            if (msCount == 10) {
                msCount = 0;
                prevSec = secCount;
                secCount++;
                setTimeViews();
            }
            if (secCount == MIN_SEC) {
                secCount = 0;
                prevMin = minCount;
                minCount++;
                setTimeViews();
            }
            if (minCount == MIN_SEC) {
                prevMin = minCount;
                minCount = 0;
                setTimeViews();
            }
        }

        // sets the TextViews to their appropriate count values
        private void setTimeViews() {

            String minutes = "00";
            String seconds = "00";

            if (minCount != prevMin) {
                minutes = (minCount < 10 ? "0" + minCount : "" + minCount);
                textMinutes.setText(minutes);
            }
            if (secCount != prevSec) {
                seconds = (secCount < 10 ? "0" + secCount : "" + secCount);
                textSeconds.setText(seconds);
            }
            textMilliseconds.setText(Integer.toString(msCount));
        }
    }
}
