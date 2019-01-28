package com.smartconsultingchallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private MenuItem mPrevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = findViewById(R.id.viewpager);

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_exercise_1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_exercise_2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_exercise_3:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_exercise_4:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new ExerciseAdapter(getSupportFragmentManager()));
    }
}
