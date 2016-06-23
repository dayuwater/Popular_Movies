package com.tanwang9408.popularmovies;

import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        String[] imageUrlArray={
                "Movie 1",
                "Movie 2",
                "Movie 3",
                "Movie 4",
                "Movie 5",
                "Movie 6",
                "Movie 7",
                "Movie 8",
                "Movie 9"



        };
        List<String> imageUrls=new ArrayList<String> (Arrays.asList(imageUrlArray));
        GridView gridView=(GridView)rootView.findViewById(R.id.gridView_movies);
        mMovieAdapter=new ArrayAdapter<String>(getActivity(),R.layout.grid_item_movie
        ,R.id.grid_item_movie_imageview,imageUrlArray);
        gridView.setAdapter(mMovieAdapter);



        return rootView;
    }
}
