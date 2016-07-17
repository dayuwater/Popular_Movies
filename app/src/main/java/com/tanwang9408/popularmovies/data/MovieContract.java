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

// used WeatherContract.java in Sunshine as the boilerplate for this file
package com.tanwang9408.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.tanwang9408.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";



    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.

    // perhaps this is useless in this project, but keep it here for now
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // name
        public static final String TABLE_NAME = "movie";
        // foreign key related
        public static final String COLUMN_MOVIE_KEY="movie_api_id";

        // other fields
        public static final String COLUMN_POSTER_PATH="poster_path";
        public static final String COLUMN_ADULT="adult";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_LANGUAGE="language";
        public static final String COLUMN_FAVORITE="favorite";
        public static final String COLUMN_IS_POPULAR="is_popular";
        public static final String COLUMN_IS_TOP_RATED="is_top_rated";
        public static final String COLUMN_POPULARITY="popularity";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }



    }


    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        // name
        public static final String TABLE_NAME = "trailer";
        // foreign key related
        public static final String COLUMN_TRAILER_KEY="trailer_api_id";
        public static final String COLUMN_MOVIE_KEY="movie_id";
        // other fields
        public static final String COLUMN_ISO_639_1="iso_639_1";
        public static final String COLUMN_ISO_3166_1="iso_3166_1";
        // intentional typo to distinguish this from the real id (trailer_id)
        public static final String COLUMN_KEA_TRAILOR="trailer_api_key";
        public static final String COLUMN_NAME="name";
        public static final String COLUMN_SITE="site";
        public static final String COLUMN_SIZE="size";
        public static final String COLUMN_TYPE="type";

        //        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerMovie(String movieApiId) {
            return CONTENT_URI.buildUpon().appendPath(movieApiId).build();
        }



        public static Uri buildTrailerMovieWithTrailerId(String movieId, String TrailerId) {
            return CONTENT_URI.buildUpon().appendPath(movieId)
                    .appendPath(TrailerId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTrailerIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

//        public static long getStartDateFromUri(Uri uri) {
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0)
//                return Long.parseLong(dateString);
//            else
//                return 0;
//        }







    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        // name
        public static final String TABLE_NAME = "review";
        // foreign key related
        public static final String COLUMN_REVIEW_KEY="review_api_id";
        public static final String COLUMN_MOVIE_KEY="movie_id";
        // other fields
        public static final String COLUMN_AUTHOR="author";
        public static final String COLUMN_CONTENT="content";
        public static final String COLUMN_URL="url";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewMovie(String movieApiId) {
            return CONTENT_URI.buildUpon().appendPath(movieApiId).build();
        }




        public static Uri buildReviewMovieWithReviewId(String movieId, String ReviewId) {
            return CONTENT_URI.buildUpon().appendPath(movieId)
                    .appendPath(ReviewId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getReviewIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        
        



    }

}
