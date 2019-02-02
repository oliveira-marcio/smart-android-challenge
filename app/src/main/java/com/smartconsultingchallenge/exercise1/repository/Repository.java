package com.smartconsultingchallenge.exercise1.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smartconsultingchallenge.exercise1.database.DatabaseHelper;
import com.smartconsultingchallenge.exercise1.network.PostalService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class Repository {

    private final String LOG = Repository.class.getSimpleName();
    private static final String SYNC_SYNCED_KEY = "sync_synced";
    private static final String SYNC_STATUS_KEY = "sync_status";
    private static final String SYNC_ERROR_KEY = "sync_message";

    private static final Object LOCK = new Object();
    private static Repository sInstance;

    private final Context mContext;
    private final PostalService mPostalService;
    private final DatabaseHelper mDb;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private MutableLiveData<String> mResults = new MutableLiveData<>();


    public Repository(Context context, PostalService service, DatabaseHelper db) {
        mContext = context;
        mPostalService = service;
        mDb = db;

        fetchAndSyncPostalCodes();
    }

    public synchronized static Repository getInstance(Context context,
                                                      PostalService service,
                                                      DatabaseHelper db) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(context, service, db);
            }
        }
        return sInstance;
    }

    public LiveData<String> getResults() {
        return mResults;
    }

    private int getTotalPostals() {
        return mDb.getTotalRows();
    }

    public void fetchAndSyncPostalCodes() {
        final int BUFFER_RESULTS = 1000;

        // TODO: Just for testing pourposes. REMOVE!!
//        setSyncResult(false, null);
        if (dataIsReady()) {
            // TODO: No need for that on final version. REMOVE!
            mResults.postValue("data alredeady loaded");
            return;
        }

        if (!isNetworkConnected()) {
            mResults.postValue(null);
            setSyncResult(false, "No internet available");
            return;
        }

        setStartSync();

        mDb.deleteAllRows();

        mDisposable.add(mPostalService.getPostalCodes()
                .flatMapObservable(new Function<ResponseBody, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final ResponseBody responseBody) {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                InputStreamReader inputStreamReader = new InputStreamReader(
                                        responseBody.byteStream(),
                                        Charset.forName("UTF-8")
                                );
                                BufferedReader reader = new BufferedReader(inputStreamReader);
                                reader.readLine(); // discard csv header
                                while (reader.ready()) {
                                    emitter.onNext(reader.readLine());
                                }
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .buffer(BUFFER_RESULTS)
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final List<String> results) {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) {
                                Log.v(LOG, "Inserting: " + results.size() + " rows.");
                                mDb.bulkInsert(results);
                                emitter.onComplete();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String results) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(LOG, e.getMessage());
                        setSyncResult(false, "Error retrieving data.");
                        mResults.postValue(null);
                    }

                    @Override
                    public void onComplete() {
                        Log.v(LOG, "COMPLETE");
                        setSyncResult(true, null);
                        mResults.postValue("COMPLETE");
                        Log.v(LOG, "" + getTotalPostals());
                    }
                })
        );
    }

    private boolean isNetworkConnected() {
        boolean isConected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        isConected = conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isConnected();
        return isConected;
    }

    private boolean dataIsReady() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getBoolean(SYNC_SYNCED_KEY, false);
    }

    public String getSyncError() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(SYNC_ERROR_KEY, null);
    }

    private void setStartSync() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SYNC_STATUS_KEY, true);
        spe.apply();
    }

    public boolean getSyncStatus() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean result = sp.getBoolean(SYNC_STATUS_KEY, false);
        return result;
    }

    private void setSyncResult(boolean syncStatus, String errorMessage) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SYNC_SYNCED_KEY, syncStatus);
        spe.putString(SYNC_ERROR_KEY, errorMessage);
        spe.putBoolean(SYNC_STATUS_KEY, false);
        spe.apply();
    }

    public void clear() {
        mDisposable.clear();
    }
}
