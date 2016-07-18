package com.tanwang9408.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tanwang9408.popularmovies.data.MovieContract;

import org.w3c.dom.Text;

/**
 * Created by tanwang on 7/16/16.
 */
public class ReviewAdapter extends CursorAdapter {

    private static final String LOG_TAG=TrailerAdapter.class.getSimpleName();
    public ReviewAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_item_review,parent,false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int idx_author= cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        TextView authorView=(TextView)view.findViewById(R.id.list_item_review_author);
        authorView.setText(cursor.getString(idx_author));

        int idx_content= cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
        TextView contentView=(TextView)view.findViewById(R.id.list_item_review_content);
        contentView.setText(cursor.getString(idx_content));


//        int idx_uRL= cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_URL);
//        TextView uRLView=(TextView)view.findViewById(R.id.list_item_review_url);
//        uRLView.setText(cursor.getString(idx_uRL));






    }
}
