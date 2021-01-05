package com.example.labtwoppo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import Data.Timer;
import ViewModels.AppViewModel;

public class ListFragment extends Fragment {

    private AppViewModel appViewModel;
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        Button addSetButton = view.findViewById(R.id.add_set_button);
        /*addSetButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_sequenceFragment_to_timerFragment);
        });*/

        Button startTimerButton = view.findViewById(R.id.startTimerButton);
        startTimerButton.setOnClickListener(view1 -> {
             timer = new Timer((long)10000, (long)1000, appViewModel);
             timer.start();
        });

        TextView timerValue = view.findViewById(R.id.timerValue);


        appViewModel.getTimerValue().observe(getViewLifecycleOwner(), value -> {
            timerValue.setText(String.valueOf(value));
        });

        return view;
    }
}