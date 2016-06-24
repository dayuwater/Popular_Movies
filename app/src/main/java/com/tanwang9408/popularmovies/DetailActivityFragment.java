package com.tanwang9408.popularmovies;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent=getActivity().getIntent();
        TextView plotView=(TextView)rootView.findViewById(R.id.plotView);
        plotView.setText(intent.getStringArrayExtra(Intent.EXTRA_TEXT)[2]);
        TextView titleView=(TextView)rootView.findViewById(R.id.titleView);
        titleView.setText(intent.getStringArrayExtra(Intent.EXTRA_TEXT)[0]);
        TextView urlView=(TextView)rootView.findViewById(R.id.posterURLView);
        urlView.setText(intent.getStringArrayExtra(Intent.EXTRA_TEXT)[1]);
        TextView ratingView=(TextView)rootView.findViewById(R.id.ratingView);
        ratingView.setText(intent.getStringArrayExtra(Intent.EXTRA_TEXT)[3]+"/10");
        TextView releaseView=(TextView)rootView.findViewById(R.id.releasedateView);
        releaseView.setText(intent.getStringArrayExtra(Intent.EXTRA_TEXT)[4]);

        return rootView;
    }
}
