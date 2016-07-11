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

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class MovieContract {

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

    // TODO: Add a table for the movies
    public static final class MovieEntry implements BaseColumns {
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


    }

    // TODO: Add a table for the trailers
    public static final class TrailerEntry implements BaseColumns {
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





    }
    // TODO: Add a table for the reviews
    public static final class ReviewEntry implements BaseColumns {
        // name
        public static final String TABLE_NAME = "review";
        // foreign key related
        public static final String COLUMN_REVIEW_KEY="review_api_id";
        public static final String COLUMN_MOVIE_KEY="movie_id";
        // other fields
        public static final String COLUMN_AUTHOR="author";
        public static final String COLUMN_CONTENT="content";
        public static final String COLUMN_URL="url";



    }
    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
    }
}
