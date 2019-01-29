package com.smartconsultingchallenge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.smartconsultingchallenge.exercise1.Exercise1Fragment;
import com.smartconsultingchallenge.exercise2.Exercise2Fragment;
import com.smartconsultingchallenge.exercise3.Exercise3Fragment;
import com.smartconsultingchallenge.exercise4.Exercise4Fragment;

public class ExerciseAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;

    public ExerciseAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Exercise1Fragment();
            case 1:
                return new Exercise2Fragment();
            case 2:
                return new Exercise3Fragment();
            default:
                return new Exercise4Fragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
