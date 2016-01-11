package com.example.aikosh.seproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Sqlite.FeedEntry.TABLE_NAME + " (" +
                    Sqlite.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Sqlite.FeedEntry.ID + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.Relations + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.x_cordinate + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.y_cordinate + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.Floor + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.Type + TEXT_TYPE + COMMA_SEP +
                    Sqlite.FeedEntry.LongText + TEXT_TYPE +
                    //   Sqlite.FeedEntry.updatedAt + TEXT_TYPE +
                    " )";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE " + Sqlite.FeedEntry.TABLE_NAME;
    final String TAG = "SqliteHelper";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Created");
    }

    public void onCreate(SQLiteDatabase db) {
        if (MainActivity.prefs.getBoolean("firstrun", true)) {
            db.execSQL("DROP TABLE IF EXISTS " + Sqlite.FeedEntry.TABLE_NAME);
            db.execSQL(SQL_CREATE_ENTRIES);
            Log.d(TAG, "Database created");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// This database is only a cache for online data, so its upgrade policy is
// to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void putInfo(SqliteHelper helper, String ID, List<Integer> Relations, int x_cordinate, int y_cordinate, int Floor, String Type,String LongText) {
// Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        int[] array = new int[Relations.size() + 1];
        for (int i = 0; i < Relations.size(); i++) array[i] = Relations.get(i);
        values.put(Sqlite.FeedEntry.ID, ID);
        values.put(Sqlite.FeedEntry.Relations, Arrays.toString(array));
        values.put(Sqlite.FeedEntry.x_cordinate, x_cordinate);
        values.put(Sqlite.FeedEntry.y_cordinate, y_cordinate);
        values.put(Sqlite.FeedEntry.Floor, Floor);
        values.put(Sqlite.FeedEntry.Type, Type);
        values.put(Sqlite.FeedEntry.LongText,LongText);
        db.insert(Sqlite.FeedEntry.TABLE_NAME, null, values);
        Log.d(TAG, "One row inserted");
    }

    public int getSize() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement c = db.compileStatement("SELECT count(*) FROM " + Sqlite.FeedEntry.TABLE_NAME);
        return (int) c.simpleQueryForLong();

    }
    public Cursor getData(int i)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Sqlite.FeedEntry.TABLE_NAME + " WHERE " + Sqlite.FeedEntry._ID + " = " + i, null);
        c.moveToFirst();
        return c;
    }
}