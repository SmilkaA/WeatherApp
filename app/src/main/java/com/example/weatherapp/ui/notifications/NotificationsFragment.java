package com.example.weatherapp.ui.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherapp.R;
import com.example.weatherapp.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Weather channel", "Weather", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        final Button button = binding.sendButtonDashboard;
        button.setOnClickListener(view -> {
            NotificationCompat.Builder notificationComBuilder = new NotificationCompat.Builder(getActivity().getApplicationContext(), "Weather channel");
            notificationComBuilder.setContentTitle("Weather App");
            notificationComBuilder.setContentText("Hello ^_^");
            notificationComBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            notificationComBuilder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity().getApplicationContext());
            managerCompat.notify(1, notificationComBuilder.build());

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}