package com.tanwang9408.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tanwang9408.popularmovies.data.MovieContract;

import java.util.List;

/**
 * Created by tanwang on 6/27/16.
 */
public class PicassoImageAdapter extends CursorAdapter {
    private static final String LOG_TAG=PicassoImageAdapter.class.getSimpleName();
    private static final String IMAGE_PATH="http://image.tmdb.org/t/p/w500";
    public PicassoImageAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imgView=(ImageView)view;
        int idx_img_url=cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        Picasso.with(context).setIndicatorsEnabled(true);
        Picasso.with(context).load(IMAGE_PATH+cursor.getString(idx_img_url)).into(imgView);




    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view=LayoutInflater.from(context).inflate(R.layout.grid_item_movie,parent,false);
        return view;
    }


}
