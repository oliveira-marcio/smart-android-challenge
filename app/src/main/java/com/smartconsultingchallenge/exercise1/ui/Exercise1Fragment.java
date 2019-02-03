package com.smartconsultingchallenge.exercise1.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartconsultingchallenge.R;
import com.smartconsultingchallenge.exercise1.utils.Injector;
import com.smartconsultingchallenge.exercise1.viewmodel.MainViewModel;
import com.smartconsultingchallenge.exercise1.viewmodel.MainViewModelFactory;

public class Exercise1Fragment extends Fragment {

    private MainViewModel mViewModel;
    private LinearLayout mDataView;
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter = new CustomAdapter();
    private LinearLayout mLoadingView;
    private LinearLayout mEmptyView;
    private TextView mErrorView;
    private Button mRefreshButon;

    public Exercise1Fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise1, container, false);

        mDataView = view.findViewById(R.id.dataView);
        mRecyclerView = view.findViewById(R.id.postals_rv);
        mLoadingView = view.findViewById(R.id.loading_view);
        mEmptyView = view.findViewById(R.id.empty_view);
        mErrorView = view.findViewById(R.id.error_view);
        mRefreshButon = view.findViewById(R.id.refresh_button);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        MainViewModelFactory factory = Injector
                .provideMainViewModelFactory(getActivity());

        mViewModel = ViewModelProviders
                .of(getActivity(), factory)
                .get(MainViewModel.class);

        mViewModel.getResults().observe(this, new Observer<Cursor>() {
            @Override
            public void onChanged(@Nullable Cursor cursor) {
                mAdapter.swapCursor(cursor);
                displayResults(cursor != null && cursor.moveToFirst(), mViewModel.getSyncError());
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
        mLoadingView.setVisibility(View.VISIBLE);
        mDataView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
    }

    private void displayResults(boolean isSuccess, String errorMessage) {
        if (mViewModel.dataIsLoading()) {
            UILoading();
            return;
        }

        mLoadingView.setVisibility(View.GONE);
        mDataView.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        mErrorView.setText(errorMessage);
    }
}
