package com.tanwang9408.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.tanwang9408.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();

            //fail("0"+","+"false"+","+sqlBitCompare(expectedValue,valueCursor.getString(idx)));

            assertTrue("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, sqlBitCompare(expectedValue, valueCursor.getString(idx)));

        }
    }

    static boolean sqlBitCompare(String a, String b){
        if(a.equals(b)){
            return true;
        }
        else{
            if(a.equals("0")&&b.equals("false")||b.equals("0")&&a.equals("false")){
                return true;
            }
            else if(a.equals("1")&&b.equals("true")||b.equals("1")&&a.equals("true")){
                return true;
            }
            else{
                return false;
            }
        }
    }

    /*
        Students: Use this to create some default movie values for your database tests.
     */
    static ContentValues createTrailerValues(long movieRowId) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
        movieValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, "dhjewdewd");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, "6391");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, "31661");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_KEA_TRAILOR, "keat");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "name");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_SITE, "site");
        movieValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, 1024);
        movieValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "type");




        return movieValues;
    }
    
    static ContentValues createReviewVaules(long movieRowId){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        movieValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_KEY, "jshbqdjhjd");
        movieValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "author");
        movieValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "content");
        movieValues.put(MovieContract.ReviewEntry.COLUMN_URL, "url");
        


        return movieValues;
        
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createOneMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Title");
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 99.9);
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,"2012/12/12");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, 223344);
        testValues.put(MovieContract.MovieEntry.COLUMN_ADULT, false);
        testValues.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, "en");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "overview");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "path");


        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertOneMovie(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createOneMovie();

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
