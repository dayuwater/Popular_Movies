package com.tanwang9408.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tanwang on 7/13/16.
 */
public class Utility {

    public static String getJsonStringFromUri(URL url) throws IOException {
        // Create the request to OpenWeatherMap, and open the connection

        HttpURLConnection urlConnection=null;
        BufferedReader reader=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("111", "Error closing stream", e);
                }
            }

        }


    }

    public static String getPreferredCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static boolean sqlBitCompare(String a, String b){
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
     
}
