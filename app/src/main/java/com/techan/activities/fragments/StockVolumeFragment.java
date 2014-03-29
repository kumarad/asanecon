package com.techan.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.techan.R;

public class StockVolumeFragment extends Fragment {
    public static final String VOLUME = "VOLUME";
    public static final String AVG_VOLUME = "AVG_VOLUME";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_vol, container, false);
        Bundle args = getArguments();

        TextView volView = (TextView) rootView.findViewById(R.id.detailVol);
        volView.setText("Volume: ");
        TextView volValView = (TextView) rootView.findViewById(R.id.detailVolVal);
        volValView.setText(args.getString(VOLUME));


        TextView avgVolView = (TextView) rootView.findViewById(R.id.detailAvgVol);
        avgVolView.setText("Avg Volume: ");
        TextView avgVolValView = (TextView) rootView.findViewById(R.id.detailAvgVolVal);
        avgVolValView.setText(args.getString(AVG_VOLUME));

        return rootView;
    }

}
