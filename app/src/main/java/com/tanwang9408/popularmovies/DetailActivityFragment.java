package com.tanwang9408.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
