package com.emrehmrc.harcamalarm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emrehmrc.harcamalarm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TotalFragment extends Fragment {

    private static final String TAG = "TotalFragment";
    int year;

    public TotalFragment() {
        // Required empty public constructor
    }

    public static TotalFragment newInstances(int page) {

        TotalFragment h = new TotalFragment();
        Bundle arg = new Bundle();
        arg.putInt("page", page);
        h.setArguments(arg);
        return h;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        year = getArguments().getInt("page", 2);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_total, container, false);
    }

}
