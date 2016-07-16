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
public class TrailerAdapter extends CursorAdapter {

    private static final String LOG_TAG=TrailerAdapter.class.getSimpleName();
    public TrailerAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_item_trailer,parent,false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int idx_name= cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME);
        TextView nameView=(TextView)view.findViewById(R.id.list_item_trailer_name);
        nameView.setText(cursor.getString(idx_name));

        int idx_size= cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SIZE);
        TextView sizeView=(TextView)view.findViewById(R.id.list_item_trailer_name);
        sizeView.setText(cursor.getString(idx_name));

        int idx_site= cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_SITE);
        TextView siteView=(TextView)view.findViewById(R.id.list_item_trailer_site);
        siteView.setText(cursor.getString(idx_site));

        int idx_type= cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TYPE);
        TextView typeView=(TextView)view.findViewById(R.id.list_item_trailer_type);
        typeView.setText(cursor.getString(idx_type));

        int idx_key= cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEA_TRAILOR);
        TextView keyView=(TextView)view.findViewById(R.id.list_item_trailer_key);
        keyView.setText(cursor.getString(idx_key));




    }
}
