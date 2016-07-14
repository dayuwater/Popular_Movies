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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tanwang9408.popularmovies.data.MovieContract;
import com.tanwang9408.popularmovies.data.MovieContract.MovieEntry;
import com.tanwang9408.popularmovies.data.MovieContract.ReviewEntry;

import com.tanwang9408.popularmovies.data.MovieContract.TrailerEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, MovieInfo[]> {

    private PicassoImageAdapter mMovieAdapter;
    public static MovieInfo[] mMovieInfo;
    private final Context mContext;
    public static final String APPID=AppID.API;

    private final String LOG_TAG=FetchMovieTask.class.getSimpleName();

    public FetchMovieTask(Context context, PicassoImageAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }



    @Override
    protected void onPostExecute(MovieInfo[] movieInfos) {
        super.onPostExecute(movieInfos);
        mMovieInfo= movieInfos;
//        mMovieAdapter.clear();
//        for(MovieInfo info : movieInfos){
//            mMovieAdapter.add(info.imgUrl);
//        }
    }


    @Override
    protected MovieInfo[] doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.

        if(params.length==0){
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url;
            if(params[0].equals("popular")) {
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/popular?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
            }
            else if(params[0].equals("toprated")){
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/top_rated?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
            }
            else{
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/top_rated?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                Log.e(LOG_TAG,"Entered favorite collection.");

            }


            //forecastJsonStr = buffer.toString();
            forecastJsonStr=Utility.getJsonStringFromUri(url);
            JSONObject jo=new JSONObject(forecastJsonStr);
            JSONArray movieArray=jo.getJSONArray("results");
            int arrLength=movieArray.length();
            // the movie data should be ready at this point
            // begin enter the data into database


            //  fetch the trailer database from api
            for(int i = 0; i < arrLength; i++) {
                long movieId=movieArray.getJSONObject(i).getLong("id");
                long movidId=addMovie(movieArray.getJSONObject(i),false); // the boolean won't take effect if the movie is already in the database
                // TODO: query the trailer by api
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/videos?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                String trailerJsonStr=Utility.getJsonStringFromUri(url);
                JSONObject trailerObject=new JSONObject(trailerJsonStr);

                // TODO: generate the content
                // create the value
                Vector<ContentValues> cVVector = new Vector<ContentValues>(arrLength);
                JSONArray trailerArray=trailerObject.getJSONArray("results");
                for(int j=0; j<trailerArray.length();j++) {
                    ContentValues trailerValues = new ContentValues();
                    JSONObject trailerObj=trailerArray.getJSONObject(j);
                    trailerValues.put(TrailerEntry.COLUMN_KEA_TRAILOR, trailerObj.getString("key"));
                    trailerValues.put(TrailerEntry.COLUMN_ISO_3166_1, trailerObj.getString("iso_3166_1"));
                    trailerValues.put(TrailerEntry.COLUMN_ISO_639_1, trailerObj.getString("iso_639_1"));
                    trailerValues.put(TrailerEntry.COLUMN_SITE, trailerObj.getString("site"));
                    trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, movidId);
                    trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, trailerObj.getString("id"));
                    trailerValues.put(TrailerEntry.COLUMN_NAME, trailerObj.getString("name"));
                    trailerValues.put(TrailerEntry.COLUMN_SIZE, trailerObj.getInt("size"));
                    trailerValues.put(TrailerEntry.COLUMN_TYPE, trailerObj.getString("type"));


                    cVVector.add(trailerValues);
                }
                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvv=new ContentValues[cVVector.size()];
                    cVVector.toArray(cvv);
                    mContext.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI,cvv);

                }


            }





            // fetch the review database from api
            for(int i = 0; i < arrLength; i++) {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(arrLength);
                long movieId=movieArray.getJSONObject(i).getLong("id");
                long movidId=addMovie(movieArray.getJSONObject(i),false);
                // TODO: query the trailer by api
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/reviews?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                String reviewJsonStr=Utility.getJsonStringFromUri(url);
                JSONObject reviewObject=new JSONObject(reviewJsonStr);
                // TODO: generate the content
                cVVector = new Vector<ContentValues>(arrLength);
                JSONArray reviewArray=reviewObject.getJSONArray("results");
                for(int j=0; j<reviewArray.length();j++) {
                    ContentValues reviewValues = new ContentValues();
                    JSONObject reviewObj=reviewArray.getJSONObject(j);
                    reviewValues.put(ReviewEntry.COLUMN_AUTHOR, reviewObj.getString("author"));
                    reviewValues.put(ReviewEntry.COLUMN_CONTENT, reviewObj.getString("content"));
                    reviewValues.put(ReviewEntry.COLUMN_URL, reviewObj.getString("url"));

                    reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movidId);
                    reviewValues.put(ReviewEntry.COLUMN_REVIEW_KEY, reviewObj.getString("id"));



                    cVVector.add(reviewValues);
                }

                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvv=new ContentValues[cVVector.size()];
                    cVVector.toArray(cvv);
                    mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI,cvv);

                }
            }









            MovieInfo[] results=new MovieInfo[arrLength];

            for(int i=0; i<arrLength;i++) {
                results[i]=new MovieInfo();


                results[i].title = jo.getJSONArray("results").getJSONObject(i).getString("title");


                results[i].imgUrl = jo.getJSONArray("results").getJSONObject(i).getString("poster_path");

                results[i].plot = jo.getJSONArray("results").getJSONObject(i).getString("overview");

                results[i].rating = jo.getJSONArray("results").getJSONObject(i).getDouble("vote_average");

                results[i].date = jo.getJSONArray("results").getJSONObject(i).getString("release_date");
            }

            return results;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            //return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    long addMovie(JSONObject jo, boolean favorite) throws JSONException {
        long movidId;
        long movieId=jo.getLong("id");

        // check if the movie is already in the database
        Cursor movieCursor=mContext.getContentResolver().query(MovieEntry.CONTENT_URI,new String[]{MovieEntry._ID},
                MovieEntry.COLUMN_MOVIE_KEY+" = ?",new String[]{Long.toString(movieId)},null);
        if(movieCursor.moveToFirst()){
            int movieIdIndex=movieCursor.getColumnIndex(MovieEntry._ID);
            movidId=movieCursor.getLong(movieIdIndex);
        }
        else{
            // create the value
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_TITLE, jo.getString("title"));
            movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, jo.getString("poster_path"));
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, jo.getString("overview"));

            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, jo.getString("release_date"));
            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, jo.getDouble("vote_average"));
            movieValues.put(MovieEntry.COLUMN_LANGUAGE, jo.getString("original_language"));

            movieValues.put(MovieEntry.COLUMN_MOVIE_KEY, jo.getLong("id"));
            movieValues.put(MovieEntry.COLUMN_ADULT, jo.getBoolean("adult"));

            // insert into database
            Uri insertedUri=mContext.getContentResolver().insert(MovieEntry.CONTENT_URI,movieValues);
            movidId= ContentUris.parseId(insertedUri);

        }



        movieCursor.close();
        return movidId;






    }




    //    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//    private ArrayAdapter<String> mForecastAdapter;
//    private final Context mContext;
//
//    public FetchWeatherTask(Context context, ArrayAdapter<String> forecastAdapter) {
//        mContext = context;
//        mForecastAdapter = forecastAdapter;
//    }
//
//    private boolean DEBUG = true;
//
//    /* The date/time conversion code is going to be moved outside the asynctask later,
//     * so for convenience we're breaking it out into its own method now.
//     */
//    private String getReadableDateString(long time){
//        // Because the API returns a unix timestamp (measured in seconds),
//        // it must be converted to milliseconds in order to be converted to valid date.
//        Date date = new Date(time);
//        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
//        return format.format(date).toString();
//    }
//
//    /**
//     * Prepare the weather high/lows for presentation.
//     */
//    private String formatHighLows(double high, double low) {
//        // Data is fetched in Celsius by default.
//        // If user prefers to see in Fahrenheit, convert the values here.
//        // We do this rather than fetching in Fahrenheit so that the user can
//        // change this option without us having to re-fetch the data once
//        // we start storing the values in a database.
//        SharedPreferences sharedPrefs =
//                PreferenceManager.getDefaultSharedPreferences(mContext);
//        String unitType = sharedPrefs.getString(
//                mContext.getString(R.string.pref_units_key),
//                mContext.getString(R.string.pref_units_metric));
//
//        if (unitType.equals(mContext.getString(R.string.pref_units_imperial))) {
//            high = (high * 1.8) + 32;
//            low = (low * 1.8) + 32;
//        } else if (!unitType.equals(mContext.getString(R.string.pref_units_metric))) {
//            Log.d(LOG_TAG, "Unit type not found: " + unitType);
//        }
//
//        // For presentation, assume the user doesn't care about tenths of a degree.
//        long roundedHigh = Math.round(high);
//        long roundedLow = Math.round(low);
//
//        String highLowStr = roundedHigh + "/" + roundedLow;
//        return highLowStr;
//    }
//
//    /**
//     * Helper method to handle insertion of a new location in the weather database.
//     *
//     * @param locationSetting The location string used to request updates from the server.
//     * @param cityName A human-readable city name, e.g "Mountain View"
//     * @param lat the latitude of the city
//     * @param lon the longitude of the city
//     * @return the row ID of the added location.
//     */
//    long addLocation(String locationSetting, String cityName, double lat, double lon) {
//        // Students: First, check if the location with this city name exists in the db
//        // If it exists, return the current ID
//        // Otherwise, insert it using the content resolver and the base URI
//        return -1;
//    }
//
//    /*
//        Students: This code will allow the FetchWeatherTask to continue to return the strings that
//        the UX expects so that we can continue to test the application even once we begin using
//        the database.
//     */
//    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
//        // return strings to keep UI functional for now
//        String[] resultStrs = new String[cvv.size()];
//        for ( int i = 0; i < cvv.size(); i++ ) {
//            ContentValues weatherValues = cvv.elementAt(i);
//            String highAndLow = formatHighLows(
//                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP),
//                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP));
//            resultStrs[i] = getReadableDateString(
//                    weatherValues.getAsLong(WeatherEntry.COLUMN_DATE)) +
//                    " - " + weatherValues.getAsString(WeatherEntry.COLUMN_SHORT_DESC) +
//                    " - " + highAndLow;
//        }
//        return resultStrs;
//    }
//
//    /**
//     * Take the String representing the complete forecast in JSON Format and
//     * pull out the data we need to construct the Strings needed for the wireframes.
//     *
//     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//     * into an Object hierarchy for us.
//     */
//    private String[] getWeatherDataFromJson(String forecastJsonStr,
//                                            String locationSetting)
//            throws JSONException {
//
//        // Now we have a String representing the complete forecast in JSON Format.
//        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
//        // into an Object hierarchy for us.
//
//        // These are the names of the JSON objects that need to be extracted.
//
//        // Location information
//        final String OWM_CITY = "city";
//        final String OWM_CITY_NAME = "name";
//        final String OWM_COORD = "coord";
//
//        // Location coordinate
//        final String OWM_LATITUDE = "lat";
//        final String OWM_LONGITUDE = "lon";
//
//        // Weather information.  Each day's forecast info is an element of the "list" array.
//        final String OWM_LIST = "list";
//
//        final String OWM_PRESSURE = "pressure";
//        final String OWM_HUMIDITY = "humidity";
//        final String OWM_WINDSPEED = "speed";
//        final String OWM_WIND_DIRECTION = "deg";
//
//        // All temperatures are children of the "temp" object.
//        final String OWM_TEMPERATURE = "temp";
//        final String OWM_MAX = "max";
//        final String OWM_MIN = "min";
//
//        final String OWM_WEATHER = "weather";
//        final String OWM_DESCRIPTION = "main";
//        final String OWM_WEATHER_ID = "id";
//
//        try {
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
//            String cityName = cityJson.getString(OWM_CITY_NAME);
//
//            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
//            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
//            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
//
//            long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);
//
//            // Insert the new weather information into the database
//            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // These are the values that will be collected.
//                long dateTime;
//                double pressure;
//                int humidity;
//                double windSpeed;
//                double windDirection;
//
//                double high;
//                double low;
//
//                String description;
//                int weatherId;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//
//                pressure = dayForecast.getDouble(OWM_PRESSURE);
//                humidity = dayForecast.getInt(OWM_HUMIDITY);
//                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
//                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
//
//                // Description is in a child array called "weather", which is 1 element long.
//                // That element also contains a weather code.
//                JSONObject weatherObject =
//                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//                weatherId = weatherObject.getInt(OWM_WEATHER_ID);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                high = temperatureObject.getDouble(OWM_MAX);
//                low = temperatureObject.getDouble(OWM_MIN);
//
//                ContentValues weatherValues = new ContentValues();
//
//                weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
//                weatherValues.put(WeatherEntry.COLUMN_DATE, dateTime);
//                weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
//                weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
//                weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
//                weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
//                weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
//                weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
//                weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
//                weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
//
//                cVVector.add(weatherValues);
//            }
//
//            // add to database
//            if ( cVVector.size() > 0 ) {
//                // Student: call bulkInsert to add the weatherEntries to the database here
//            }
//
//            // Sort order:  Ascending, by date.
//            String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
//            Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
//                    locationSetting, System.currentTimeMillis());
//
//            // Students: Uncomment the next lines to display what what you stored in the bulkInsert
//
////            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri,
////                    null, null, null, sortOrder);
////
////            cVVector = new Vector<ContentValues>(cur.getCount());
////            if ( cur.moveToFirst() ) {
////                do {
////                    ContentValues cv = new ContentValues();
////                    DatabaseUtils.cursorRowToContentValues(cur, cv);
////                    cVVector.add(cv);
////                } while (cur.moveToNext());
////            }
//
//            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");
//
//            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
//            return resultStrs;
//
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    protected String[] doInBackground(String... params) {
//
//        // If there's no zip code, there's nothing to look up.  Verify size of params.
//        if (params.length == 0) {
//            return null;
//        }
//        String locationQuery = params[0];
//
//        // These two need to be declared outside the try/catch
//        // so that they can be closed in the finally block.
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader = null;
//
//        // Will contain the raw JSON response as a string.
//        String forecastJsonStr = null;
//
//        String format = "json";
//        String units = "metric";
//        int numDays = 14;
//
//        try {
//            // Construct the URL for the OpenWeatherMap query
//            // Possible parameters are avaiable at OWM's forecast API page, at
//            // http://openweathermap.org/API#forecast
//            final String FORECAST_BASE_URL =
//                    "http://api.openweathermap.org/data/2.5/forecast/daily?";
//            final String QUERY_PARAM = "q";
//            final String FORMAT_PARAM = "mode";
//            final String UNITS_PARAM = "units";
//            final String DAYS_PARAM = "cnt";
//
//            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                    .appendQueryParameter(QUERY_PARAM, params[0])
//                    .appendQueryParameter(FORMAT_PARAM, format)
//                    .appendQueryParameter(UNITS_PARAM, units)
//                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                    .build();
//
//            URL url = new URL(builtUri.toString());
//
//            // Create the request to OpenWeatherMap, and open the connection
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            // Read the input stream into a String
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//            if (inputStream == null) {
//                // Nothing to do.
//                return null;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                // But it does make debugging a *lot* easier if you print out the completed
//                // buffer for debugging.
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                // Stream was empty.  No point in parsing.
//                return null;
//            }
//            forecastJsonStr = buffer.toString();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error ", e);
//            // If the code didn't successfully get the weather data, there's no point in attemping
//            // to parse it.
//            return null;
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "Error closing stream", e);
//                }
//            }
//        }
//
//        try {
//            return getWeatherDataFromJson(forecastJsonStr, locationQuery);
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
//            e.printStackTrace();
//        }
//        // This will only happen if there was an error getting or parsing the forecast.
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(String[] result) {
//        if (result != null && mForecastAdapter != null) {
//            mForecastAdapter.clear();
//            for(String dayForecastStr : result) {
//                mForecastAdapter.add(dayForecastStr);
//            }
//            // New data is back from the server.  Hooray!
//        }
//    }
}