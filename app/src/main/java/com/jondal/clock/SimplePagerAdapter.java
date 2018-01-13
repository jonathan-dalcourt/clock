package com.jondal.clock;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jondal.clock.fragments.AlarmFragment;
import com.jondal.clock.fragments.ClockFragment;
import com.jondal.clock.fragments.StopwatchFragment;
import com.jondal.clock.fragments.TimerFragment;

/**
 * Fragment Pager Adapter for swiping between pages
 */

public class SimplePagerAdapter extends FragmentPagerAdapter {

    public SimplePagerAdapter(FragmentManager fm) { super(fm); }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return new ClockFragment();
            case 1: return new TimerFragment();
            case 2: return new StopwatchFragment();
            case 3: return new AlarmFragment();
            default: return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0: return "Clock";
            case 1: return "Timer";
            case 2: return "Stopwatch";
            case 3: return "Alarm";
            default: return null;
        }
    }
}

