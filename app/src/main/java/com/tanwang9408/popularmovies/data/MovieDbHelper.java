/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tanwang9408.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tanwang9408.popularmovies.data.MovieContract.MovieEntry;
import com.tanwang9408.popularmovies.data.MovieContract.TrailerEntry;
import com.tanwang9408.popularmovies.data.MovieContract.ReviewEntry;


/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // TODO: Update this method for this project
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                MovieEntry.COLUMN_ADULT + " BIT NOT NULL, " +
                MovieEntry.COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +

                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +


                // Set up the -- column as a foreign key to location table.
                //" FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                //LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one movie entry per id
                // , it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_KEY  +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                TrailerEntry.COLUMN_ISO_639_1 + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_ISO_3166_1 + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_KEA_TRAILOR + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +

                TrailerEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +

                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL,"+


                // Set up the -- column as a foreign key to location table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +

                // To assure the application have just one movie entry per id
                // , it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_KEY  +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_KEY + " TEXT NOT NULL," +

                ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +



                // Set up the -- column as a foreign key to location table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +

                // To assure the application have just one movie entry per id
                // , it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_KEY  +
                ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);


    }

    // TODO: Update this method for this project

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
