package com.smartconsultingchallenge.exercise1.utils;

import android.content.Context;

import com.smartconsultingchallenge.exercise1.database.DatabaseHelper;
import com.smartconsultingchallenge.exercise1.network.PostalClient;
import com.smartconsultingchallenge.exercise1.network.PostalService;
import com.smartconsultingchallenge.exercise1.repository.Repository;
import com.smartconsultingchallenge.exercise1.viewmodel.MainViewModelFactory;

public class Injector {
    public static Repository provideRepository(Context context) {
        PostalService service = PostalClient.getInstance();
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        return Repository.getInstance(context.getApplicationContext(), service, database);
    }

    public static MainViewModelFactory provideMainViewModelFactory(Context context) {
        Repository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }
}
