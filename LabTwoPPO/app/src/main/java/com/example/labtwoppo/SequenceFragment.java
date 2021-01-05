package com.example.labtwoppo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.Random;

import Data.SequenceDatabase;
import Models.Sequence;
import ViewModels.AppViewModel;

public class SequenceFragment extends Fragment {

    AppViewModel appViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sequence, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarSeq);

        final int[] colour = {getRandomColor()};

        Button btnColor = view.findViewById(R.id.btnChooseColorSeq);
        Button btnBack = view.findViewById(R.id.btnBackFromSeqToList);
        Button btnSaveNewSeq = view.findViewById(R.id.btnSaveNewSeq);

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog.Builder(getActivity())
                        .setTitle(getString(R.string.chooseColor))
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton(getString(R.string.confirmColor),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        colour[0] = envelope.getColor();
                                        toolbar.setBackgroundColor(envelope.getColor());
                                        btnBack.setBackgroundColor(envelope.getColor());
                                        btnColor.setBackgroundColor(envelope.getColor());
                                        btnSaveNewSeq.setBackgroundColor(envelope.getColor());
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancelColor),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(true)
                        .attachBrightnessSlideBar(true)
                        .setBottomSpace(12)
                        .show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_sequenceFragment_to_itemFragment);
            }
        });

        btnSaveNewSeq.setOnClickListener(v -> {
            String name = appViewModel.getName(((TextView)view.findViewById(R.id.seqNameTextView)).getText().toString());
            int preparation = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.preparationNum)).getText().toString());
            int work = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.workNum)).getText().toString());
            int rest = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.restNum)).getText().toString());
            int cycles = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.cyclesNum)).getText().toString());
            int sets = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.setsNum)).getText().toString());
            int restBetweenSets = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.restBetweenSetsNum)).getText().toString());
            int calm = appViewModel.getNumberFromString(((TextView)view.findViewById(R.id.calmNum)).getText().toString());
            Sequence sequence = new Sequence(name, preparation, work, rest, cycles, sets, restBetweenSets, calm, colour[0]);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    SequenceDatabase.getDatabase(getContext()).timerDao().insert(sequence);
                }
            });
            Navigation.findNavController(view).navigate(R.id.action_sequenceFragment_to_itemFragment);
        });

        return view;
    }


    private int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}