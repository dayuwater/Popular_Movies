package com.tanwang9408.popularmovies;

import android.content.Intent;
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

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private String mSortOrder;
    private final String MOVIEFRAGMENT_TAG = "MMTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Stetho.initializeWithDefaults(this);
        mSortOrder = Utility.getPreferredCriteria(this);


        //OkHttpClient client = new OkHttpClient();
        //client.networkInterceptors().add(new StethoInterceptor());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()

                    .add(R.id.fragment, new MainActivityFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }





    }

    @Override
    protected void onResume() {
        super.onResume();
        String order = Utility.getPreferredCriteria(this);
        // update the location in our second pane using the fragment manager
        if (order != null && !order.equals(mSortOrder)) {
            MainActivityFragment ff = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if (null != ff) {
                ff.onOrderChanged();
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
}
