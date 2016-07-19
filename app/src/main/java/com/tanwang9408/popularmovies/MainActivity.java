package com.tanwang9408.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.picasso.Picasso;
import com.tanwang9408.popularmovies.data.MovieContract;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.CallBack {

    private String mSortOrder;
    private final String MOVIEFRAGMENT_TAG = "MMTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    private long mLastMovieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);
        mSortOrder = Utility.getPreferredCriteria(this);



        //OkHttpClient client = new OkHttpClient();
        //client.networkInterceptors().add(new StethoInterceptor());

        if(findViewById(R.id.movie_detail_container)!=null){

            mTwoPane=true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()



                        .replace (R.id.movie_detail_container,new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();


            }


        }
        else{
            mTwoPane=false;
        }







    }

    @Override
    protected void onResume() {
        super.onResume();
        String order = Utility.getPreferredCriteria(this);

        // update the location in our second pane using the fragment manager
        if (order != null && !order.equals(mSortOrder)) {
            MainActivityFragment ff = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_grid_movie);
            if (null != ff) {
                ff.onOrderChanged();
            }
            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                //df.onMovieChanged();
            }
            mSortOrder = order;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        mLastMovieId=Long.parseLong(MovieContract.MovieEntry.getMovieIdFromUri(movieUri));
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, movieUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }

    }
}
