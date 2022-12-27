package com.example.weatherapp.ui.dashboard;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.weatherapp.databinding.FragmentDashboardBinding;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.networking.WeatherAPI;

import org.json.JSONObject;

import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText editText = binding.inputDashboard;
        final Button button = binding.searchButtonDashboard;
        button.setOnClickListener(view -> {
            if (validate(String.valueOf(editText.getText()))) {
                getWeather();
            }

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public boolean validate(String text) {
        if (text.matches("")) {
            Toast.makeText(getActivity().getApplicationContext(), "You did not enter a city name", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private void getWeather() {
        AndroidNetworking.get(WeatherAPI.BASEURL + WeatherAPI.CurrentWeather + "q=London,uk&units=metric&appid=4a4697bee1747a834c2d866b2179dc6f")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity().getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity().getApplicationContext(), "222", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}