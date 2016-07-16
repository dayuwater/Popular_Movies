package com.tanwang9408.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tanwang9408.popularmovies.data.MovieContract;
import com.tanwang9408.popularmovies.data.MovieContract.MovieEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String IMAGE_PATH= "http://image.tmdb.org/t/p/w185";
    private String mMovieString;
    private ShareActionProvider mShareActionProvider;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final String POPULAR_MOVIE_SHARE_HASHTAG = " #PopularMovie";

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;


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
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String movieId=MovieEntry.getMovieIdFromUri(intent.getData());

        // build the uri for trailer

        Uri trailerUri= MovieContract.TrailerEntry.buildTrailerMovie(movieId);

        // build the uri for movie
        Uri reviewUri= MovieContract.ReviewEntry.buildReviewMovie(movieId);


        if(id==DETAIL_LOADER) {

            return new CursorLoader(
                    getActivity(),
                    intent.getData(),
                    null,

                    null,
                    null,
                    null
            );
        }
        else if(id==TRAILER_LOADER){

            return new CursorLoader(
                    getActivity(),
                    trailerUri,
                    null,

                    null,
                    null,
                    null
            );

        }
        else{
            return new CursorLoader(
                    getActivity(),
                    reviewUri,
                    null,

                    null,
                    null,
                    null
            );

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // load detail cursor
        if(loader.getId()==DETAIL_LOADER) {

            if (!data.moveToFirst()) {
                return;
            }


            // set direct table items
            getActivity().setTitle(data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE)));
            TextView titleView = (TextView) getView().findViewById(R.id.titleView);
            titleView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_TITLE)));

            TextView ratingView = (TextView) getView().findViewById(R.id.ratingView);
            ratingView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
            TextView releaseView = (TextView) getView().findViewById(R.id.releasedateView);
            releaseView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
            ImageView imageView = (ImageView) getView().findViewById(R.id.posterView);
            Picasso.with(getContext()).load(IMAGE_PATH + data.getString(data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)))
                    .into(imageView);
            TextView plotView = (TextView) getView().findViewById(R.id.plotView);
            plotView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));
        }
        else if(loader.getId()==TRAILER_LOADER) {

            if (!data.moveToFirst()) {
                return;
            }



            mTrailerAdapter.swapCursor(data);


            // set trailer items
            ListView listViewTrailer = (ListView) getView().findViewById(R.id.trailerView);

            listViewTrailer.setAdapter(mTrailerAdapter);

            //TODO: set click listener

        }

        else {

            if (!data.moveToFirst()) {
                return;
            }


            mReviewAdapter.swapCursor(data);
            // set review items
            ListView listViewReview = (ListView) getView().findViewById(R.id.reviewView);

            listViewReview.setAdapter(mReviewAdapter);

            //TODO: set click listener

        }






    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerAdapter.swapCursor(null);
        mReviewAdapter.swapCursor(null);

    }
}
