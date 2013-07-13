package com.techan.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.techan.R;
import com.techan.custom.Util;

// Fragment representing a section of the app.
public class StockPeFragment extends Fragment {
    public static final String PE_VAL = "PE_VAL";
    public static final String PEG_VAL = "PEG_VAL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_pe, container, false);
        Bundle args = getArguments();

        TextView peView = (TextView) rootView.findViewById(R.id.detailPe);
        peView.setText("PE: ");
        TextView peValView = (TextView) rootView.findViewById(R.id.detailPeVal);
        peValView.setText(args.getString(PE_VAL));

        TextView pegView = (TextView) rootView.findViewById(R.id.detailPeg);
        pegView.setText("PEG: ");
        TextView pegValView = (TextView) rootView.findViewById(R.id.detailPegVal);
        pegValView.setText(args.getString(PEG_VAL));

        return rootView;
    }
}
