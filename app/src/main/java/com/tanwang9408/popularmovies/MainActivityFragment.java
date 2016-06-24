package com.tanwang9408.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static final String APPID="878337c301e790447564e6a9915721e5";

    // For popular: http://api.themoviedb.org/3/movie/popular?api_key=878337c301e790447564e6a9915721e5
    // For top rated: http://api.themoviedb.org/3/movie/top_rated?api_key=878337c301e790447564e6a9915721e5

    private ArrayAdapter<String> mMovieAdapter;

    private MovieInfo[] mMovieInfo;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_main, container, false);

        List<String> imageUrls=new ArrayList<String> ();
        GridView gridView=(GridView)rootView.findViewById(R.id.gridView_movies);
        mMovieAdapter=new ArrayAdapter<String>(getActivity(),R.layout.grid_item_movie
        ,R.id.grid_item_movie_imageview,imageUrls);
        gridView.setAdapter(mMovieAdapter);
        refresh();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openDetail=new Intent(getActivity(),DetailActivity.class);
                openDetail.putExtra(Intent.EXTRA_TEXT,mMovieInfo[position].toStringArray());

                startActivity(openDetail);

            }
        });


        return rootView;
    }

    private void showToastMessage(String text) {
        Context context = getContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        FetchMovieTask fetch=new FetchMovieTask();
        fetch.execute(true);
    }

    // Since there is only two options for the movie list in this project, I would use boolean to indicate user choice
    // true=popular, false=top_rated
    public class FetchMovieTask extends AsyncTask<Boolean,Void,MovieInfo[]>{
        private final String LOG_TAG=FetchMovieTask.class.getSimpleName();

        @Override
        protected void onPostExecute(MovieInfo[] movieInfos) {
            super.onPostExecute(movieInfos);
            mMovieInfo= movieInfos;
            mMovieAdapter.clear();
            for(MovieInfo info : movieInfos){
                mMovieAdapter.add(info.title);
            }
        }

        @Override
        protected MovieInfo[] doInBackground(Boolean... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url;
                if(params[0]) {
                    Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/popular?").buildUpon().
                            appendQueryParameter("api_key",APPID).build();
                    url = new URL(uri.toString());
                }
                else{
                    Uri uri= Uri.parse("http://api.themoviedb.org/3/movie/top_rated?").buildUpon().
                            appendQueryParameter("api_key",APPID).build();
                    url = new URL(uri.toString());
                }

                // Create the request to OpenWeatherMap, and open the connection
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
                forecastJsonStr = buffer.toString();

                JSONObject jo=new JSONObject(forecastJsonStr);
                int arrLength=jo.getJSONArray("results").length();
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
    }
}
