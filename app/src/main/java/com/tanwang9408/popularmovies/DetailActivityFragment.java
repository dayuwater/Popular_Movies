package com.tanwang9408.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tanwang9408.popularmovies.data.MovieContract.MovieEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String IMAGE_PATH= "http://image.tmdb.org/t/p/w185";
    private String mMovieString;
    private ShareActionProvider mShareActionProvider;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final String POPULAR_MOVIE_SHARE_HASHTAG = " #PopularMovie";

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
                            MovieEntry.TABLE_NAME   + "."  +  MovieEntry._ID,
                              MovieEntry.COLUMN_IS_POPULAR,
                              MovieEntry.COLUMN_POSTER_PATH,
                              MovieEntry.COLUMN_OVERVIEW,
                              MovieEntry.COLUMN_LANGUAGE,
            MovieEntry.COLUMN_ADULT,
            MovieEntry.COLUMN_IS_TOP_RATED,
            MovieEntry.COLUMN_MOVIE_KEY,
                      };

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);









        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                null,

                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }


        getActivity().setTitle(data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE)));
        TextView titleView=(TextView)getView().findViewById(R.id.titleView);
        titleView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE)));

        TextView ratingView=(TextView)getView().findViewById(R.id.ratingView);
        ratingView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
        TextView releaseView=(TextView)getView().findViewById(R.id.releasedateView);
        releaseView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
        ImageView imageView=(ImageView)getView().findViewById(R.id.posterView);
        Picasso.with(getContext()).load(IMAGE_PATH +data.getString(data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)))
        .into(imageView);
        TextView plotView=(TextView)getView().findViewById(R.id.plotView);
        plotView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));





    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
