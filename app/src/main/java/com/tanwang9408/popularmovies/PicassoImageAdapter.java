package com.tanwang9408.popularmovies;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tanwang on 6/27/16.
 */
public class PicassoImageAdapter extends ArrayAdapter<String> {
    private static final String LOG_TAG=PicassoImageAdapter.class.getSimpleName();
    private static final String IMAGE_PATH="http://image.tmdb.org/t/p/w500";
    public PicassoImageAdapter(Activity context, List<String> imageUrls){
        super(context,0,imageUrls);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String imageUrl=getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView imgView=(ImageView)convertView.findViewById(R.id.grid_item_movie_imageview);
        Picasso.with(getContext()).load(IMAGE_PATH+imageUrl).into(imgView);

        return convertView;
    }
}
