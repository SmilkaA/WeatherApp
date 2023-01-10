package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MapDetailsFragment extends Fragment {

    //TODO: ask Taras why returns null
    public static MapDetailsFragment newInstance(String cityName) {
        Bundle args = new Bundle();
        args.putString("cityName", cityName);
        MapDetailsFragment fragment = new MapDetailsFragment();
        new MapDetailsFragment().setArguments(args);
        return fragment;
    }

    public MapDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_details, container, false);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView output = view.findViewById(R.id.weather_details_in_fragment);
        if (getArguments() != null) {
            output.setText(getArguments().getString("cityName"));
        }
        return view;
    }
}