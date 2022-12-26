package com.example.weatherapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.databinding.FragmentDashboardBinding;
import com.example.weatherapp.model.Response;
import com.example.weatherapp.model.Weather;
import com.example.weatherapp.networking.WeatherAPI;
import com.example.weatherapp.networking.WeatherAPICalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private WeatherAPICalls apiInterface;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText editText = binding.inputDashboard;

        apiInterface = WeatherAPI.getClient().create(WeatherAPICalls.class);

        final Button button = binding.searchButtonDashboard;
        button.setOnClickListener(view -> {
            if (validate(String.valueOf(editText.getText()))) {
                Call<Response> call = apiInterface.getWeatherByCityName(String.valueOf(editText.getText()), WeatherAPI.API_KEY);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        Response apiResponse = response.body();
                        List<Weather> weather = apiResponse.getWeather();
                        String weatherDetails = weather.get(0).getDescription();
                        Toast.makeText(getContext(), weatherDetails, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
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
}