package com.jondal.clock.subactivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jondal.clock.R;
import com.jondal.clock.fragments.ClockFragment;

/**
 * Activity to hold Clock Fragment
 */

public class ClockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ClockFragment()).commit();
    }
}
