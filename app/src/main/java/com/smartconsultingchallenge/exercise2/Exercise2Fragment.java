package com.smartconsultingchallenge.exercise2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.smartconsultingchallenge.R;
import com.squareup.picasso.Picasso;

public class Exercise2Fragment extends Fragment {

    private ImageView mHeader;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private int mMaxHeaderScroll = 0;

    public Exercise2Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise2, container, false);

        mToolbar = view.findViewById(R.id.toolbar);
        mHeader = view.findViewById(R.id.header_image);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // We need to wait for Toolbar to be drawed, so we can retrieve its height and properly set
        // header size, start scroll listener and define the first list item offset, so it won't be
        // covered by header
        final ViewTreeObserver observer = mToolbar.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setUpHeader();
                setupScrollListener();
                mRecyclerView.setAdapter(new CustomAdapter(mHeader.getLayoutParams().height));
                mToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        return view;
    }

    /**
     * Set header size in a 2:1 format and load image from URL. Also defines maximum header scroll
     * (mMaxHeaderScroll) as header height minus toolbar height.
     */
    private void setUpHeader() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

        int headerWidth = metrics.widthPixels;
        int headerHeight = headerWidth / 2;

        mHeader.getLayoutParams().width = headerWidth;
        mHeader.getLayoutParams().height = headerHeight;

        mMaxHeaderScroll = headerHeight - mToolbar.getHeight();

        final String IMAGE_URL = "http://www.empregoestagios.com/wp-content/uploads/2016/12/smart-660x330.jpg";
        Picasso.get().load(IMAGE_URL).placeholder(R.drawable.image_placeholder).into(mHeader);
    }

    /**
     * Create scroll listener to RecyclerView that moves header up or down up to a certain point
     * (mMaxHeaderScroll) and make toolbar proportionally transparent.
     */
    private void setupScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int newTranslationY;
                if (dy > 0) {
                    newTranslationY = (int) Math.max(-mMaxHeaderScroll, mHeader.getTranslationY() - dy);
                } else {
                    newTranslationY = (int) Math.min(0, mHeader.getTranslationY() - dy);
                }
                mHeader.setTranslationY(newTranslationY);
                updateToolBarColor((float) -newTranslationY / (float) mMaxHeaderScroll);
            }
        });
    }

    /**
     * Set Toolbar transparency percentage level between 0 (transparent) and 1 (opaque)
     */
    private void updateToolBarColor(float transparency) {
        int barColor = ActivityCompat.getColor(getActivity(), R.color.colorPrimary);
        int transparencyColor = Color.argb((int) (255 * constrain(transparency, 0, 1)),
                Color.red(barColor),
                Color.green(barColor),
                Color.blue(barColor));

        mToolbar.setBackgroundColor(transparencyColor);
    }

    /**
     * Helper method to constrain a maximum and minimum value for a specif value
     */
    private float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }
}
