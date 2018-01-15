package com.jondal.clock.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jondal.clock.AlarmReceiver;
import com.jondal.clock.ButtonEvent;
import com.jondal.clock.R;

import java.util.Calendar;

/**
 * Alarm
 */

public class AlarmFragment extends Fragment {

    // Log Tag for this activity
    private static final String LOG_TAG = AlarmFragment.class.getName();

    // constants for unit conversions
    private static final int HOU_SEC = 3600;
    private static final int MIN_SEC = 60;
    private static final int SEC_MSEC = 1000;
    private static final int MIN_MSEC = MIN_SEC * SEC_MSEC;
    private static final int HOU_MSEC = HOU_SEC * SEC_MSEC;

    private static final int NUM_HOURS = 12;

    private Intent alarmIntent;

    private Button cancelButton;
    private Button createButton;
    private Button setButton;
    private Button cancelPickButton;
    private TimePicker timePicker;
    private TextView currentTime;

    // static variables for use in Alarm Receiver
    public static final String NO_ALARM = "No alarm set";
    public static Button staticCancelButton;
    public static Button staticCreateButton;
    public static TextView staticCurrentTime;

    private RelativeLayout currentTimeLayout;

    private AlarmManager am;
    private PendingIntent alarmPending;

    private int hour;
    private int minute;
    private long alarmTime;

    public AlarmFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        currentTime = rootView.findViewById(R.id.alarm_current);
        createButton = rootView.findViewById(R.id.alarm_create);
        setButton = rootView.findViewById(R.id.alarm_set);
        cancelButton = rootView.findViewById(R.id.alarm_cancel);
        cancelPickButton = rootView.findViewById(R.id.alarm_cancel_pick);
        staticCreateButton = createButton;
        staticCancelButton = cancelButton;
        staticCurrentTime = currentTime;
        currentTimeLayout = rootView.findViewById(R.id.current_time);

        timePicker = rootView.findViewById(R.id.time_picker);
        setTimePicker();

        initViews();
        if (isAlarmActive()) { changeButtonState(ButtonEvent.CREATE); }

        // sets the TimePicker and Set Alarm Views to visible
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimePicker();
                changeVisibility(ButtonEvent.CREATE);
                changeButtonState(ButtonEvent.CREATE);
                Log.e(LOG_TAG, "create");
            }
        });

        // schedules an alarm
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(ButtonEvent.START);
                changeButtonState(ButtonEvent.START);
                createAlarm();
                Log.e(LOG_TAG, "set");
            }
        });

        // cancels the scheduled alarm
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(ButtonEvent.CANCEL);
                changeButtonState(ButtonEvent.CANCEL);
                cancelAlarm();
                Log.e(LOG_TAG, "cancel");
            }
        });

        // exits from the TimePicker
        cancelPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(ButtonEvent.CANCEL_PICK);
                changeButtonState(ButtonEvent.CANCEL_PICK);
            }
        });

        return rootView;
    }

    // checks to see if any alarm is active
    private boolean isAlarmActive() {

        return PendingIntent.getBroadcast(getContext(), 0, alarmIntent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    // initialises all buttons and Views
    private void initViews() {

        currentTimeLayout.setVisibility(View.VISIBLE);
        timePicker.setVisibility(View.GONE);
        createButton.setVisibility(View.VISIBLE);
        setButton.setVisibility(View.GONE);

        createButton.setEnabled(true);
        setButton.setEnabled(false);
        cancelButton.setEnabled(false);

    }

    // sets the time of the picker to the current time
    private void setTimePicker() {
        Calendar calendar = Calendar.getInstance();
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));
    }

    // schedules an alarm
    private void createAlarm() {

        getTime();

        alarmPending = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmPending);

        Log.e(LOG_TAG, "Alarm set");
        Log.e(LOG_TAG, "Seconds until alarm: " + ((alarmTime - System.currentTimeMillis()) / SEC_MSEC));
        Toast.makeText(getActivity(), R.string.alarm_toast_set, Toast.LENGTH_SHORT).show();
    }

    // gets the time to execute the alarm
    private void getTime() {

        Calendar calendar = Calendar.getInstance();

        hour = timePicker.getHour();
        minute = timePicker.getMinute();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        setAlarmText();

        alarmTime = calendar.getTimeInMillis();
    }

    // displays the time the currently set alarm will go off
    private void setAlarmText() {

        boolean isAM = hour < NUM_HOURS;
        hour = hour > NUM_HOURS ? hour - NUM_HOURS : hour;

        String amPM = isAM ? " AM" : " PM";
        String hourString = hour > NUM_HOURS ? "" + (hour - NUM_HOURS): "" + hour;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String timeString = hourString + ":" + minuteString + amPM;
        currentTime.setText(timeString);
    }

    // cancels the currently schedules alarm, if any
    private void cancelAlarm() {

        am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmPending);

        Log.e(LOG_TAG, "Alarm cancelled");
        Toast.makeText(getActivity(), R.string.alarm_toast_cancel, Toast.LENGTH_SHORT).show();
    }

    // enables and disables buttons
    public void changeButtonState(ButtonEvent event) {

        switch (event) {
            case CREATE:
                createButton.setEnabled(false);
                cancelButton.setEnabled(false);
                setButton.setEnabled(true);
                break;
            case START:
                createButton.setEnabled(false);
                setButton.setEnabled(false);
                cancelButton.setEnabled(true);
                break;
            case CANCEL_PICK:
            case CANCEL:
                createButton.setEnabled(true);
                setButton.setEnabled(false);
                cancelButton.setEnabled(false);
                break;
            default:
                Log.e(LOG_TAG, "unexpected event: " + event);
        }
    }

    // changes the visibility of Views
    private void changeVisibility(ButtonEvent event) {

        switch (event) {
            case CREATE:
                currentTimeLayout.setVisibility(View.GONE);
                timePicker.setVisibility(View.VISIBLE);
                createButton.setVisibility(View.GONE);
                setButton.setVisibility(View.VISIBLE);
                cancelPickButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
                break;
            case CANCEL_PICK:
            case START:
                currentTimeLayout.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.GONE);
                createButton.setVisibility(View.VISIBLE);
                setButton.setVisibility(View.GONE);
                cancelPickButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                break;
            case CANCEL:
                currentTime.setText(NO_ALARM);
                break;
            default:
                Log.e(LOG_TAG, "unexpected event: " + event);
        }
    }

    @Override
    public void onStop() { super.onStop(); }
}
