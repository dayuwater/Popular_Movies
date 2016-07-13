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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */

    // TODO: Modify the test for this project
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);


        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // test the movie table

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.MovieEntry._ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ADULT);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_LANGUAGE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_KEY);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_ISO_639_1);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_ISO_3166_1);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_KEA_TRAILOR);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SITE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SIZE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TYPE);


        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required trailer entry columns",
                locationColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewColumnHashSet = new HashSet<String>();
        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_KEY);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_URL);


        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required review entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testMovieTable() {
        insertMovie();


    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testTrailerTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.
        long movieRowId;
        movieRowId=insertMovie();
        if(movieRowId==-1){
            fail("Null pointer returned");
            return;
        }



        // First step: Get reference to writable database
        MovieDbHelper dbHelper=new MovieDbHelper(mContext);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues cv=TestUtilities.createTrailerValues(movieRowId);

        // Insert ContentValues into database and get a row ID back
        long rowId=db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,cv);

        // Query the database and receive a Cursor back
        Cursor c=db.query(MovieContract.TrailerEntry.TABLE_NAME,null,null,null,null,null,null);

        // Move the cursor to a valid database row
        assertTrue( "Error: No Records returned from movie query", c.moveToFirst() );



        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Trailer Query Validation Failed",c,cv);
        assertFalse( "Error: More than one record returned from trailer query",c.moveToNext());


        // Finally, close the cursor and database
        c.close();
        db.close();

    }

    public void testReviewTable(){
        long movieRowId;
        movieRowId=insertMovie();
        if(movieRowId==-1){
            fail("Null pointer returned");
            return;
        }

        MovieDbHelper dbHelper=new MovieDbHelper(mContext);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues cv=TestUtilities.createReviewVaules(movieRowId);

        long rowId=db.insert(MovieContract.ReviewEntry.TABLE_NAME,null,cv);

        Cursor c=db.query(MovieContract.ReviewEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue( "Error: No Records returned from movie query", c.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Review Query Validation Failed",c,cv);
        assertFalse( "Error: More than one record returned from review query",c.moveToNext());

        c.close();
        db.close();









    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertMovie() {
        // First step: Get reference to writable database
        MovieDbHelper dbHelper=new MovieDbHelper(mContext);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues=TestUtilities.createOneMovie();

        // Insert ContentValues into database and get a row ID back
        long movieRowId;
        movieRowId=TestUtilities.insertOneMovie(mContext);

        assertTrue(movieRowId!=-1);
        // Query the database and receive a Cursor back
        Cursor c=db.query(MovieContract.MovieEntry.TABLE_NAME,null,null,null,null,null,null);


        // Move the cursor to a valid database row
        assertTrue( "Error: No Records returned from movie query", c.moveToFirst() );

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",c,testValues);
        assertFalse( "Error: More than one record returned from movie query",c.moveToNext());



        // Finally, close the cursor and database
        c.close();
        db.close();

        return movieRowId;
    }
}
