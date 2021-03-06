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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.tanwang9408.popularmovies.data.MovieContract.MovieEntry;
import com.tanwang9408.popularmovies.data.MovieContract.TrailerEntry;
import com.tanwang9408.popularmovies.data.MovieContract.ReviewEntry;


/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.delete(TrailerEntry.TABLE_NAME, null, null);
        db.delete(ReviewEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        
        // trailer

        String type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);

        assertEquals("",
                TrailerEntry.CONTENT_TYPE, type);

        String testMovie = "movie_api_id";

        type = mContext.getContentResolver().getType(
                TrailerEntry.buildTrailerMovie(testMovie));

        assertEquals("",
                TrailerEntry.CONTENT_TYPE, type);

        String testTrailerId = "trailer_api_id";

        type = mContext.getContentResolver().getType(
                TrailerEntry.buildTrailerMovieWithTrailerId(testMovie, testTrailerId));

        assertEquals("",
                TrailerEntry.CONTENT_ITEM_TYPE, type);
        
        // movie


        type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);

        assertEquals("",
                MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);

        assertEquals("",
                TrailerEntry.CONTENT_TYPE, type);
        
        // review

        String testReviewId = "review_api_id";

        type = mContext.getContentResolver().getType(
                ReviewEntry.buildReviewMovie(testMovie));

        assertEquals("",
                ReviewEntry.CONTENT_TYPE, type);



        type = mContext.getContentResolver().getType(
                ReviewEntry.buildReviewMovieWithReviewId(testMovie, testReviewId));

        assertEquals("",
                ReviewEntry.CONTENT_ITEM_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicTrailerQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createOneMovie();
        long locationRowId = TestUtilities.insertOneMovie(mContext);


        ContentValues trailerValues = TestUtilities.createTrailerValues(locationRowId);

        long weatherRowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue("", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("", trailerCursor, trailerValues);
    }

    public void testBasicReviewQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createOneMovie();
        long locationRowId = TestUtilities.insertOneMovie(mContext);


        ContentValues reviewValues = TestUtilities.createReviewVaules(locationRowId);

        long weatherRowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("", reviewCursor, reviewValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicMovieQueries() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createOneMovie();
        long locationRowId = TestUtilities.insertOneMovie(mContext);

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicLocationQueries, location query", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("",
                    movieCursor.getNotificationUri(), MovieEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createOneMovie();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieEntry._ID, movieRowId);
        updatedValues.put(MovieEntry.COLUMN_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createOneMovie();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        
        ContentValues trailerValues = TestUtilities.createTrailerValues(locationRowId);
        ContentValues reviewValues=TestUtilities.createReviewVaules(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);

        Uri trailerInsertUri = mContext.getContentResolver()
                .insert(TrailerEntry.CONTENT_URI, trailerValues);
        assertTrue(trailerInsertUri != null);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewInsertUri != null);
        
        

        
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
                trailerCursor, trailerValues);


        trailerValues.putAll(testValues);

        // Get the joined Trailer and Movie data
        trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.buildTrailerMovie(TestUtilities.TEST_MOVIE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Trailer and Movie Data.",
                trailerCursor, trailerValues);

        

        // Get the joined Trailer and Movie data with a Trailer Id
        trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.buildTrailerMovieWithTrailerId(
                        TestUtilities.TEST_MOVIE, TestUtilities.TEST_TRAILER_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Trailer and Movie Data with trailer id.",
                trailerCursor, trailerValues);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewCursor, reviewValues);

        reviewValues.putAll(testValues);

        // Get the joined Review and Movie data
        reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewMovie(TestUtilities.TEST_MOVIE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Review and Movie Data.",
                reviewCursor, reviewValues);

        // Get the joined Review and Movie data with a Review Id
        reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildReviewMovieWithReviewId(TestUtilities.TEST_MOVIE, TestUtilities.TEST_REVIEW_ID),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Review and Movie data for a review id.",
                reviewCursor, reviewValues);


        
        
    }


    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our trailer delete.
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, trailerObserver);

        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();


        movieObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertTrailerValues(long locationRowId) {
        String trailerId = TestUtilities.TEST_TRAILER_ID;
        int a=0;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, a++ ) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, locationRowId);
            trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, trailerId+Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_TYPE, Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_SIZE, a);
            trailerValues.put(TrailerEntry.COLUMN_NAME, Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_SITE, Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_ISO_639_1, Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_ISO_3166_1, Integer.toString(a));
            trailerValues.put(TrailerEntry.COLUMN_KEA_TRAILOR, Integer.toString(a));

            returnContentValues[i] = trailerValues;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues(long locationRowId) {
        String reviewId = TestUtilities.TEST_REVIEW_ID;
        int a=0;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, a++ ) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, locationRowId);
            reviewValues.put(ReviewEntry.COLUMN_REVIEW_KEY, reviewId+Integer.toString(a));
            reviewValues.put(ReviewEntry.COLUMN_URL, Integer.toString(a));
            reviewValues.put(ReviewEntry.COLUMN_CONTENT, a);
            reviewValues.put(ReviewEntry.COLUMN_AUTHOR, Integer.toString(a));
           

            returnContentValues[i] = reviewValues;
        }
        return returnContentValues;
    }


    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createOneMovie();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating MovieEntry.",
                cursor, testValues);

       
        ContentValues[] bulkInsertContentValues = createBulkInsertTrailerValues(movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, trailerObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, bulkInsertContentValues);


        trailerObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                TrailerEntry.COLUMN_TRAILER_KEY + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating TrailerEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();

        bulkInsertContentValues = createBulkInsertReviewValues(movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);
        insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, bulkInsertContentValues);


        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                ReviewEntry.COLUMN_REVIEW_KEY + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating ReviewEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
