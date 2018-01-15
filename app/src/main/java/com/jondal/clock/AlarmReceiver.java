package com.jondal.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.jondal.clock.fragments.AlarmFragment;
import com.jondal.clock.subactivities.AlarmActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom Broadcast Receiver for an Alarm
 */

public class AlarmReceiver extends BroadcastReceiver {

    // Log Tag for this activity
    private static final String LOG_TAG = AlarmReceiver.class.getName();

    private static final int ONE_SEC = 1000;
    private static final int RINGTONE_TIME = 5 * ONE_SEC;

    private Ringtone r;

    @Override
    public void onReceive(Context context, Intent intent) {

        notify(context);
        cancelAlarm(context);
        updateViews();
    }

    // sends a notification to the user depending on their audio settings
    private void notify(Context context) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
                vibrate(context);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                playRingtone(context);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
        }

        Toast.makeText(context, R.string.alarm_toast_finished, Toast.LENGTH_LONG).show();
    }

    // vibrates the phone for one second
    private void vibrate(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(ONE_SEC);
    }

    // plays a ringtone when the alarm finishes
    private void playRingtone(Context context) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // backup ringtone if the user has not set an alarm ringtone
        if (alert == null){
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        r = RingtoneManager.getRingtone(context, alert);
        r.play();

        // schedules the ringtone to stop playing in RINGTONE_TIME seconds
        Date future = new Date(System.currentTimeMillis() + RINGTONE_TIME);
        Timer stopRingtone = new Timer(true);
        stopRingtone.schedule(new RingtoneTimer(), future);
    }

    // cancels the currently schedules alarm, if any
    private static void cancelAlarm(Context context) {

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPending = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmPending);
    }

    // update the Views in the alarm fragment
    private void updateViews() {

        AlarmFragment.staticCancelButton.setEnabled(false);
        AlarmFragment.staticCreateButton.setEnabled(true);
        AlarmFragment.staticCurrentTime.setText(AlarmFragment.NO_ALARM);
    }

    // stops the Ringtone
    private class RingtoneTimer extends TimerTask {

        @Override
        public void run() {
            if (r != null) {
                r.stop();
            }
        }
    }
}
