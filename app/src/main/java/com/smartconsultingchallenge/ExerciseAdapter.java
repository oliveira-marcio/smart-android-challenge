package com.smartconsultingchallenge;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
