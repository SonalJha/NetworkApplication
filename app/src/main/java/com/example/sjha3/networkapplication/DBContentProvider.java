package com.example.sjha3.networkapplication;

/**
 * Created by sjha3 on 8/1/17.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

    public class DBContentProvider extends ContentProvider {
        static final String PROVIDER_NAME = "com.example.NetworkApplication.DBContentProvider";
        static final String URL = "content://" + PROVIDER_NAME + "/song";
        static final Uri CONTENT_URI = Uri.parse(URL);

        static final String song_name = "song_name";
        static final String song_year = "song_year";
        static final String song_artist = "song_artist";

        /**
         * Database specific constant declarations
         */

        private SQLiteDatabase db;
        static final String DATABASE_NAME = "Album";
        static final String SONG_TABLE_NAME = "song";
        static final int DATABASE_VERSION = 1;
        static final String CREATE_DB_TABLE =
                " CREATE TABLE " + SONG_TABLE_NAME +
                        " (song_name PRIMARY KEY, " +
                        " song_year, " +
                        " song_artist);";

        /**
         * Helper class that actually creates and manages
         * the provider's underlying data repository.
         */

        private static class DatabaseHelper extends SQLiteOpenHelper {
            DatabaseHelper(Context context){
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_DB_TABLE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
                onCreate(db);
            }
        }

        @Override
        public boolean onCreate() {
            Context context = getContext();
            DatabaseHelper dbHelper = new DatabaseHelper(context);

            /**
             * Create a write able database which will trigger its
             * creation if it doesn't already exist.
             */

            db = dbHelper.getWritableDatabase();
            return (db == null)? false:true;
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            /**
             * Add a new student record
             */
            long rowID = db.insert(SONG_TABLE_NAME, "", values);

            /**
             * If record is added successfully
             */
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }

            throw new SQLException("Failed to add a record into " + uri);
        }

        @Override
        public Cursor query(Uri uri, String[] projection,
                            String selection,String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(SONG_TABLE_NAME);
            if (sortOrder == null || sortOrder == ""){
                /**
                 * By default sort on student names
                 */
                sortOrder = song_year;
            }

            Cursor c = qb.query(db,	projection,	selection,
                    selectionArgs,null, null, sortOrder);
            /**
             * register to watch a content URI for changes
             */
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            int count = 0;
            count = db.delete(SONG_TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        @Override
        public int update(Uri uri, ContentValues values,
                          String selection, String[] selectionArgs) {
            int count = 0;
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        /**
         * Not implemented
         * @param uri
         * @return
         */
        @Override
        public String getType(Uri uri) {
            return null;
        }
}
