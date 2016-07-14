package com.tanwang9408.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import com.tanwang9408.popularmovies.data.MovieContract;

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

    public static final String APPID=AppID.API;
    // started content provider

    private PicassoImageAdapter mMovieAdapter;
    private ArrayAdapter<String> mMovieAdapter2;

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
        String sortOrder= MovieContract.MovieEntry.COLUMN_TITLE+" ASC ";
        Cursor cur=getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,sortOrder);
        mMovieAdapter=new PicassoImageAdapter(getActivity(),cur,0);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent openDetail=new Intent(getActivity(),DetailActivity.class);
//                openDetail.putExtra(Intent.EXTRA_TEXT,FetchMovieTask.mMovieInfo[position].toStringArray());
//
//                startActivity(openDetail);

            }
        });



        refresh();


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
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

        FetchMovieTask fetch=new FetchMovieTask(getActivity(),mMovieAdapter);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String option=prefs.getString(getString(R.string.pref_sort_key),getString((R.string.pref_sort_default)));
        fetch.execute(option);
    }


}
