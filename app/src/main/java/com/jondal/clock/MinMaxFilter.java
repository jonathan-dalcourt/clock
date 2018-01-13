package com.jondal.clock;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import com.jondal.clock.fragments.TimerFragment;

/**
 * Input Filter to prevent the user from entering second and minute values outside of 0 - 59
 */

public class MinMaxFilter implements InputFilter {

    public static final String LOG_TAG = MinMaxFilter.class.getName();

    private final int MIN = 0;
    private final int MAX = 59;

    public MinMaxFilter() {}

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        try {
            int input = Integer.parseInt(spanned.toString() + charSequence.toString());
            if (isInRange(MIN, MAX, input)) {
                return null;
            }
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException " + e);
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
