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
package com.tanwang9408.popularmovies;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.tanwang9408.popularmovies.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

public class TestFetchWeatherTask extends AndroidTestCase{

    JSONObject jo=new JSONObject();


    /*
        Students: uncomment testAddLocation after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddMovie() throws JSONException {
        jo.put("title","title");
        jo.put("poster_path","poster_path");
        jo.put("overview","overview");
        jo.put("release_date","release_date");
        jo.put("vote_average",99.9);
        jo.put("original_language","original_language");
        jo.put("id",223344);
        jo.put("adult",false);
        // start from a clean state
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{"223344"});

        FetchMovieTask fwt = new FetchMovieTask(getContext(), null);
        long movieId = fwt.addMovie(jo,false);

        // does addMovie return a valid record ID?
        assertFalse("Error: addMovie returned an invalid ID on insert",
                movieId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our movie?
            Cursor movieCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_FAVORITE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
                            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                            MovieContract.MovieEntry.COLUMN_TITLE,
                            MovieContract.MovieEntry.COLUMN_ADULT,
                            MovieContract.MovieEntry.COLUMN_LANGUAGE,
                            MovieContract.MovieEntry.COLUMN_OVERVIEW,
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
                    },
                    MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ?",
                    new String[]{"223344"},
                    null);

            // these match the indices of the projection
            if (movieCursor.moveToFirst()) {
                assertEquals("Error: the queried value of movieId does not match the returned value" +
                        "from addMovie", movieCursor.getLong(0), movieId);
                assertEquals("Error: the queried value of movie setting is incorrect",
                        movieCursor.getInt(1),0 );
                assertEquals("Error: the queried value of movie city is incorrect",
                        movieCursor.getInt(2), 223344);
                assertEquals("Error: the queried value of latitude is incorrect",
                        movieCursor.getDouble(3), 99.9);
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(4), "title");
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getInt(5), 0);
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(6), "original_language");
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(7), "overview");
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(8), "poster_path");
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(9), "release_date");

            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a movie query",
                    movieCursor.moveToNext());

            // add the movie again
            long newMovieId = fwt.addMovie(jo,false);

            assertEquals("Error: inserting a movie again should return the same ID",
                    movieId, newMovieId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_KEY+ " = ?",
                new String[]{"223344"});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
