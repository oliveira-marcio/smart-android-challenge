package com.smartconsultingchallenge.exercise1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_CLIENT;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_COUNTY_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_DISTRICT_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_LOCAL_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_LOCAL_NAME;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_ACCESS;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_LOCAL;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_NAME;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_NUMBER;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_TITLE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PLACE_TYPE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_DESIG;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_POSTAL_EXT_CODE;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PREP1;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.COLUMN_PREP2;
import static com.smartconsultingchallenge.exercise1.database.DatabaseContract.PostalEntry.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String LOG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "postals.db";
    private static final int DATABASE_VERSION = 1;

    private static final Object LOCK = new Object();
    private static DatabaseHelper sInstance;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DatabaseHelper(context);
            }
        }
        return sInstance;
    }

    public Cursor queryData(String selection, String[] selectionArgs) {
        String[] projection = {
                COLUMN_POSTAL_CODE,
                COLUMN_POSTAL_EXT_CODE,
                COLUMN_LOCAL_NAME
        };

        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
    }

    public void deleteAllRows() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public void bulkInsert(List<String> results) {
        String columns[] = {
                COLUMN_DISTRICT_CODE,
                COLUMN_COUNTY_CODE,
                COLUMN_LOCAL_CODE,
                COLUMN_LOCAL_NAME,
                COLUMN_PLACE_CODE,
                COLUMN_PLACE_TYPE,
                COLUMN_PREP1,
                COLUMN_PLACE_TITLE,
                COLUMN_PREP2,
                COLUMN_PLACE_NAME,
                COLUMN_PLACE_LOCAL,
                COLUMN_PLACE_ACCESS,
                COLUMN_PLACE_NUMBER,
                COLUMN_CLIENT,
                COLUMN_POSTAL_CODE,
                COLUMN_POSTAL_EXT_CODE,
                COLUMN_POSTAL_DESIG
        };

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (String result : results) {
                // Solution to only split on commas outside quotes
                // https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
                String resultValues[] = result.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (resultValues.length != columns.length) {
                    Log.v(LOG, "Skipping line: " + result);
                    continue;
                }
                ContentValues insertValues = new ContentValues();
                for (int i = 0; i < resultValues.length; i++) {
                    insertValues.put(columns[i], resultValues[i]);
                }
                db.insert(TABLE_NAME, null, insertValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DISTRICT_CODE + " TEXT, "
                + COLUMN_COUNTY_CODE + " TEXT, "
                + COLUMN_LOCAL_CODE + " TEXT, "
                + COLUMN_LOCAL_NAME + " TEXT, "
                + COLUMN_PLACE_CODE + " TEXT, "
                + COLUMN_PLACE_TYPE + " TEXT, "
                + COLUMN_PREP1 + " TEXT, "
                + COLUMN_PLACE_TITLE + " TEXT, "
                + COLUMN_PREP2 + " TEXT, "
                + COLUMN_PLACE_NAME + " TEXT, "
                + COLUMN_PLACE_LOCAL + " TEXT, "
                + COLUMN_PLACE_ACCESS + " TEXT, "
                + COLUMN_PLACE_NUMBER + " TEXT, "
                + COLUMN_CLIENT + " TEXT, "
                + COLUMN_POSTAL_CODE + " TEXT, "
                + COLUMN_POSTAL_EXT_CODE + " TEXT, "
                + COLUMN_POSTAL_DESIG + " TEXT);";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
