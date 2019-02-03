package com.smartconsultingchallenge.exercise1.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smartconsultingchallenge.R;
import com.smartconsultingchallenge.exercise1.database.DatabaseHelper;
import com.smartconsultingchallenge.exercise1.network.PostalService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_LOCAL_NAME;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_EXT_CODE;

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

    private MutableLiveData<Cursor> mDataResults = new MutableLiveData<>();


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

    public LiveData<Cursor> getResults() {
        return mDataResults;
    }

    /**
     * This method will sync data with server only once or in case of sync fail.
     * <p>
     * In case of local data already present (and successfully synced), a Cursor will be loaded on
     * mDataResults.
     * <p>
     * Otherwise, sync will start and results will be parsed and bulk inserted in database on each
     * 1000 results. In the end, the Cursor will be loaded.
     */
    public void fetchAndSyncPostalCodes() {
        final int BUFFER_RESULTS = 1000;

        // TODO: Just for testing pourposes. REMOVE!!
//        setSyncResult(false, null);
        if (dataIsReady()) {
            setFilteredResults(null);
            return;
        }

        if (!isNetworkConnected()) {
            mDataResults.postValue(null);
            setSyncResult(false, mContext.getString(R.string.exercise1_error_no_internet));
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
                                // TODO: FOR TESTING Pourposes. REMOVE this x var and condition!
                                int x = 0;
                                while (reader.ready()) {
                                    if (x++ > 9) break;
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
                        setSyncResult(false, mContext.getString(R.string.exercise1_error_sync_fail));
                        mDataResults.postValue(null);
                    }

                    @Override
                    public void onComplete() {
                        Log.v(LOG, "SYNC COMPLETED");
                        setSyncResult(true, null);
                        setFilteredResults(null);
                    }
                })
        );
    }

    //TODO: Remove Logs
    //TODO: Handle empty results on UI
    //TODO: When there's only one numeric, search in both
    public void setFilteredResults(String input) {
        if (input == null || input.isEmpty()) {
            mDataResults.postValue(mDb.queryData(null, null));
            return;
        }

        String[] terms = input.trim().toLowerCase().split("\\s+");
        String postalCode = "";
        String postalExtCode = "";
        List<String> localNames = new ArrayList<>();

        for (String term : terms) {
            if (term.matches("\\d+-\\d+")) {
                if (!(postalCode.isEmpty() && postalExtCode.isEmpty())) {
                    localNames.add(term);
                }
                postalCode = term.split("-")[0];
                postalExtCode = term.split("-")[1];
            } else if (term.matches("\\d+")) {
                if (postalCode.isEmpty()) {
                    postalCode = term;
                    continue;
                } else if (postalExtCode.isEmpty()) {
                    postalExtCode = term;
                } else {
                    localNames.add(term);
                }
            } else {
                localNames.add(term);
            }
        }

        Log.v("TERM", terms.length + "");

        StringBuilder queryBuilder = new StringBuilder();
        List<String> queryValues = new ArrayList<>();
        final String JOIN = " AND ";

        if (!postalCode.isEmpty()) {
            queryBuilder.append("lower(" + COLUMN_POSTAL_CODE + ") LIKE ?");
            queryBuilder.append(JOIN);
            queryValues.add("%" + postalCode + "%");
        }

        if (!postalExtCode.isEmpty()) {
            queryBuilder.append("lower(" + COLUMN_POSTAL_EXT_CODE + ") LIKE ?");
            queryBuilder.append(JOIN);
            queryValues.add("%" + postalExtCode + "%");
        }

        for (String localName : localNames) {
            queryBuilder.append("lower(" + COLUMN_LOCAL_NAME + ") LIKE ?");
            queryBuilder.append(JOIN);
            queryValues.add("%" + localName + "%");
        }

        queryBuilder.delete(queryBuilder.length() - JOIN.length(), queryBuilder.length() - 1);

        mDataResults.postValue(mDb.queryData(
                queryBuilder.toString(),
                queryValues.toArray(new String[queryValues.size()]))
        );

        String[] values = queryValues.toArray(new String[queryValues.size()]);

        Log.v("TERM", queryBuilder.toString());
        for (String value : values) {
            Log.v("TERM", value);
        }
    }

    /**
     * Check internet availability
     */
    private boolean isNetworkConnected() {
        boolean isConected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        isConected = conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isConnected();
        return isConected;
    }

    /**
     * Check success sync status (true or false)
     */
    private boolean dataIsReady() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getBoolean(SYNC_SYNCED_KEY, false);
    }

    /**
     * Set sync success status to true or false and corresponding sync error message
     * Also set syncing status to false to indicate it's not running anymore.
     */
    private void setSyncResult(boolean syncStatus, String errorMessage) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SYNC_SYNCED_KEY, syncStatus);
        spe.putString(SYNC_ERROR_KEY, errorMessage);
        spe.putBoolean(SYNC_STATUS_KEY, false);
        spe.apply();
    }

    /**
     * Get sync error message
     */
    public String getSyncError() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(SYNC_ERROR_KEY, null);
    }

    /**
     * Set syncing status to true to indicate it's running
     */
    private void setStartSync() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SYNC_STATUS_KEY, true);
        spe.apply();
    }

    /**
     * Get syncing status to indicate if it's still running
     */
    public boolean getSyncStatus() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean result = sp.getBoolean(SYNC_STATUS_KEY, false);
        return result;
    }

    public void clear() {
        mDisposable.clear();
    }
}
