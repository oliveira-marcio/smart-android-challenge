package com.smartconsultingchallenge.exercise1.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.smartconsultingchallenge.exercise1.repository.Repository;

public class MainViewModel extends ViewModel {

    private final Repository mRepository;
    private LiveData<String> mResults;

    public MainViewModel(Repository repository) {
        mRepository = repository;
        mResults = mRepository.getResults();
    }

    // TODO: Final version should return a Cursor
    public LiveData<String> getResults() {
        return mResults;
    }

    public void syncData() {
        mRepository.fetchAndSyncPostalCodes();
    }

    public boolean dataIsLoading() {
        return mRepository.getSyncStatus();
    }

    public String getError() {
        return mRepository.getSyncError();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.clear();
    }
}
