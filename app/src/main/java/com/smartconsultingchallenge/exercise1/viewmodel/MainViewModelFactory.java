package com.smartconsultingchallenge.exercise1.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.smartconsultingchallenge.exercise1.repository.Repository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;

    public MainViewModelFactory(Repository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mRepository);
    }
}
