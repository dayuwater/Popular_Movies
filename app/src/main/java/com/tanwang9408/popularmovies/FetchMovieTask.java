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

    private int option=-1;

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
                option=0;
            }
            else if(params[0].equals("toprated")){
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/top_rated?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                option=1;
            }
            else{
               return null; // if the setting is favorite, we don't need to use api at all

            }


            //forecastJsonStr = buffer.toString();
            forecastJsonStr=Utility.getJsonStringFromUri(url);
            JSONObject jo=new JSONObject(forecastJsonStr);
            JSONArray movieArray=jo.getJSONArray("results");
            int arrLength=movieArray.length();
            // the movie data should be ready at this point
            // begin enter the data into database

            // fetch movies

            long[] idArray=new long[arrLength];
            for(int i = 0; i < arrLength; i++) {

                if(option==0)
                    idArray[i]=addMovie(movieArray.getJSONObject(i),false,true,false); // the boolean won't take effect if the movie is already in the database
                else if(option==1)
                    idArray[i]=addMovie(movieArray.getJSONObject(i),false,false,true);


            }


            //  fetch the trailer database from api

            for(int i = 0; i < arrLength; i++) {
                long movieId=movieArray.getJSONObject(i).getLong("id");
                long movidId=idArray[i];

                //query the trailer by api
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/videos?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                String trailerJsonStr=Utility.getJsonStringFromUri(url);
                JSONObject trailerObject=new JSONObject(trailerJsonStr);

                // generate the content
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
                long movidId=idArray[i];

                // query the trailer by api
                Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/"+movieId+"/reviews?").buildUpon().
                        appendQueryParameter("api_key",APPID).build();
                url = new URL(uri.toString());
                String reviewJsonStr=Utility.getJsonStringFromUri(url);
                JSONObject reviewObject=new JSONObject(reviewJsonStr);
                // generate the content
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



    long addMovie(JSONObject jo, boolean favorite,boolean isPopular, boolean isTopRated) throws JSONException {
        long movidId;
        long movieId=jo.getLong("id");

        // check if the movie is already in the database
        Cursor movieCursor=mContext.getContentResolver().query(MovieEntry.CONTENT_URI,null,
                MovieEntry.COLUMN_MOVIE_KEY+" = ?",new String[]{Long.toString(movieId)},null);
        if(movieCursor.moveToFirst()){
            int idx_pop=movieCursor.getColumnIndex(MovieEntry.COLUMN_IS_POPULAR);
            int moviePopularity=movieCursor.getInt(idx_pop);
            int movieIdIndex=movieCursor.getColumnIndex(MovieEntry._ID);
            int movieTopRated=movieCursor.getInt(movieCursor.getColumnIndex(MovieEntry.COLUMN_IS_TOP_RATED));
            int movieIsFavorite=movieCursor.getInt(movieCursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE));



            // update the popularity and rate information

            movidId=movieCursor.getLong(movieIdIndex);
            if(!Utility.sqlBitCompare(Integer.toString(moviePopularity),Boolean.toString(isPopular))){
                isPopular=true;


            }

            if(!Utility.sqlBitCompare(Integer.toString(movieTopRated),Boolean.toString(isTopRated))){
                isTopRated=true;


            }
            if(movieIsFavorite==1) {

                movidId = getMovidId(jo, true, isPopular, isTopRated);
            }
            else{
                movidId = getMovidId(jo,false, isPopular, isTopRated);
            }



        }
        else{
            // create the value

            movidId = getMovidId(jo, favorite, isPopular, isTopRated);

        }



        movieCursor.close();
        return movidId;






    }

    private long getMovidId(JSONObject jo, boolean favorite, boolean isPopular, boolean isTopRated) throws JSONException {
        long movidId;ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_TITLE, jo.getString("title"));
        movieValues.put(MovieEntry.COLUMN_FAVORITE, favorite);
        movieValues.put(MovieEntry.COLUMN_IS_POPULAR, isPopular);
        movieValues.put(MovieEntry.COLUMN_IS_TOP_RATED, isTopRated);

        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, jo.getString("poster_path"));
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, jo.getString("overview"));

        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, jo.getString("release_date"));
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, jo.getDouble("vote_average"));
        movieValues.put(MovieEntry.COLUMN_LANGUAGE, jo.getString("original_language"));

        movieValues.put(MovieEntry.COLUMN_MOVIE_KEY, jo.getLong("id"));
        movieValues.put(MovieEntry.COLUMN_ADULT, jo.getBoolean("adult"));
        movieValues.put(MovieEntry.COLUMN_POPULARITY, jo.getDouble("popularity"));

        // insert into database
        Uri insertedUri=mContext.getContentResolver().insert(MovieEntry.CONTENT_URI,movieValues);
        movidId= ContentUris.parseId(insertedUri);
        return movidId;
    }



}