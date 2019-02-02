package com.smartconsultingchallenge.exercise1.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartconsultingchallenge.R;
import com.smartconsultingchallenge.exercise1.utils.Injector;
import com.smartconsultingchallenge.exercise1.viewmodel.MainViewModel;
import com.smartconsultingchallenge.exercise1.viewmodel.MainViewModelFactory;

public class Exercise1Fragment extends Fragment {

    private MainViewModel mViewModel;
    private TextView mDataView;
    private ProgressBar mProgressBar;
    private LinearLayout mEmptyView;
    private TextView mErrorView;
    private Button mRefreshButon;

    public Exercise1Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise1, container, false);

        mDataView = view.findViewById(R.id.data_view);
        mProgressBar = view.findViewById(R.id.loading_indicator);
        mEmptyView = view.findViewById(R.id.empty_view);
        mErrorView = view.findViewById(R.id.error_view);
        mRefreshButon = view.findViewById(R.id.refresh_button);

        MainViewModelFactory factory = Injector
                .provideMainViewModelFactory(getActivity());

        mViewModel = ViewModelProviders
                .of(getActivity(), factory)
                .get(MainViewModel.class);

        mViewModel.getResults().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String results) {
                mDataView.setText(results);
                displayResults(results != null && !results.isEmpty(), mViewModel.getError());
            }
        });


        mRefreshButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UILoading();
                mViewModel.syncData();
            }
        });

        return view;
    }

    private void UILoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mDataView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
    }

    private void displayResults(boolean isSuccess, String errorMessage) {
        if (mViewModel.dataIsLoading()) {
            UILoading();
            return;
        }

        mProgressBar.setVisibility(View.GONE);
        mDataView.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        mErrorView.setText(errorMessage);
    }
}
