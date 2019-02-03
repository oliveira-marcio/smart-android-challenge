package com.smartconsultingchallenge.exercise1.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;

import com.smartconsultingchallenge.exercise1.repository.Repository;

public class MainViewModel extends ViewModel {

    private final Repository mRepository;
    private LiveData<Cursor> mResults;

    public MainViewModel(Repository repository) {
        mRepository = repository;
        mResults = mRepository.getResults();
    }

    public LiveData<Cursor> getResults() {
        return mResults;
    }

    public void syncData() {
        mRepository.fetchAndSyncPostalCodes();
    }

    public boolean dataIsLoading() {
        return mRepository.getSyncStatus();
    }

    public String getSyncError() {
        return mRepository.getSyncError();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mResults.getValue() != null) {
            mResults.getValue().close();
        }
        mRepository.clear();
    }
}
