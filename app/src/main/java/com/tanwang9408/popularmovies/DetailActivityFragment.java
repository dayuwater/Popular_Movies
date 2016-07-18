package com.tanwang9408.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

    static final String DETAIL_URI = "URI";
    private Uri mUri;


    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);


        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {



        MenuItem menuitem=menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuitem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
        


    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        Cursor data;
        if(mTrailerAdapter.getItem(0)!=null) {
            data = (Cursor) mTrailerAdapter.getItem(0);
            shareIntent.putExtra(Intent.EXTRA_TEXT,"https://www.youtube.com/watch?v="+
                    data.getString(data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEA_TRAILOR))+POPULAR_MOVIE_SHARE_HASHTAG);
        }
        // in case we don't have a trailer for a movie, we will just share the title of that movie
        else{
            shareIntent.putExtra(Intent.EXTRA_TEXT,getActivity().getTitle()+POPULAR_MOVIE_SHARE_HASHTAG);
        }


        return shareIntent;

    }

    private void openTrailerURLinBrowser(String key) {



        String fullUrl="https://www.youtube.com/watch?v="+key;


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(fullUrl));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + fullUrl + ", no receiving apps installed!");
        }
    }

    void onMovieChanged(long newMovieId) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
            Uri updatedUri = MovieContract.MovieEntry.buildMovieUri(Long.parseLong(movieId));
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
            getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }
         
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

        if(null!=mUri) {

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            String movieId = MovieEntry.getMovieIdFromUri(mUri);

            // build the uri for trailer

            Uri trailerUri = MovieContract.TrailerEntry.buildTrailerMovie(movieId);

            // build the uri for movie
            Uri reviewUri = MovieContract.ReviewEntry.buildReviewMovie(movieId);


            if (id == DETAIL_LOADER) {

                return new CursorLoader(
                        getActivity(),
                        mUri,
                        null,

                        null,
                        null,
                        null
                );
            } else if (id == TRAILER_LOADER) {

                return new CursorLoader(
                        getActivity(),
                        trailerUri,
                        null,

                        null,
                        null,
                        null
                );

            } else {
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

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
            float ratingText=Float.parseFloat(data.getString(data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
            ratingView.setText(getActivity().getString(R.string.format_average_rating,ratingText));
            TextView releaseView = (TextView) getView().findViewById(R.id.releasedateView);
            releaseView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
            ImageView imageView = (ImageView) getView().findViewById(R.id.posterView);
            Picasso.with(getContext()).load(IMAGE_PATH + data.getString(data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)))
                    .into(imageView);
            TextView plotView = (TextView) getView().findViewById(R.id.plotView);
            plotView.setText(data.getString(data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));

            final Button favoriteButton=(Button)getView().findViewById(R.id.button_favorite);
            if(data.getInt(data.getColumnIndex(MovieEntry.COLUMN_FAVORITE))==0) {
                favoriteButton.setText(getString(R.string.mark_as_favorite));

            }
            else{
                favoriteButton.setText(getString(R.string.unfavorite));
            }

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.getInt(data.getColumnIndex(MovieEntry.COLUMN_FAVORITE))==1) {
                        favoriteButton.setText(getString(R.string.unfavorite));
                        ContentValues cv=new ContentValues();
                        cv.put(MovieEntry.COLUMN_FAVORITE,false);

                        getActivity().getContentResolver().update(MovieEntry.CONTENT_URI,cv,
                                MovieEntry.COLUMN_MOVIE_KEY+" = ? ",
                                new String[]{data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_KEY))});
                    }
                    else{
                        favoriteButton.setText(getString(R.string.mark_as_favorite));

                        ContentValues cv=new ContentValues();
                        cv.put(MovieEntry.COLUMN_FAVORITE,true);

                        getActivity().getContentResolver().update(MovieEntry.CONTENT_URI,cv,
                                MovieEntry.COLUMN_MOVIE_KEY+" = ? ",
                                new String[]{data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_KEY))});

                    }
                }
            });
        }
        else if(loader.getId()==TRAILER_LOADER) {

            if (!data.moveToFirst()) {
                return;
            }



            mTrailerAdapter.swapCursor(data);


            // set trailer items
            ListView listViewTrailer = (ListView) getView().findViewById(R.id.trailerView);

            listViewTrailer.setAdapter(mTrailerAdapter);

            //set click listener
            listViewTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if (cursor != null) {
                        // get the trailer youtube key
                        String key = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEA_TRAILOR));
                        openTrailerURLinBrowser(key);


                    }

                }
            });


        }

        else {

            if (!data.moveToFirst()) {
                return;
            }


            mReviewAdapter.swapCursor(data);
            // set review items
            ListView listViewReview = (ListView) getView().findViewById(R.id.reviewView);

            listViewReview.setAdapter(mReviewAdapter);



        }






    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerAdapter.swapCursor(null);
        mReviewAdapter.swapCursor(null);

    }


}
