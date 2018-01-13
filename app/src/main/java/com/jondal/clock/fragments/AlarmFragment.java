package com.jondal.clock.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jondal.clock.R;

/**
 * Alarm
 */

public class AlarmFragment  extends Fragment {

    public AlarmFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);


        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
