package com.example.labtwoppo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import Data.SequenceDatabase;
import Models.Sequence;
import ViewModels.AppViewModel;

public class TimerEditFragment extends Fragment {

    AppViewModel appViewModel;
    TextView seqNameTextView, preparationNum, workNum, restNum, cyclesNum, setsNum,
            restBetweenSetsNum, calmNum;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_timer_edit, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        seqNameTextView = (TextView)view.findViewById(R.id.seqNameTextViewUpdate);
        preparationNum = (TextView)view.findViewById(R.id.preparationNumUpdate);
        workNum = (TextView)view.findViewById(R.id.workNumUpdate);
        restNum = (TextView)view.findViewById(R.id.restNumUpdate);
        cyclesNum = (TextView)view.findViewById(R.id.cyclesNumUpdate);
        setsNum = (TextView)view.findViewById(R.id.setsNumUpdate);
        restBetweenSetsNum = (TextView)view.findViewById(R.id.restBetweenSetsNumUpdate);
        calmNum = (TextView)view.findViewById(R.id.calmNumUpdate);

        Sequence sequence = appViewModel.getCurrentSequence();
        seqNameTextView.setText(sequence.name);
        preparationNum.setText(Integer.toString(sequence.preparation));
        workNum.setText(Integer.toString(sequence.work));
        restNum.setText(Integer.toString(sequence.rest));
        cyclesNum.setText(Integer.toString(sequence.cycles));
        setsNum.setText(Integer.toString(sequence.sets));
        restBetweenSetsNum.setText(Integer.toString(sequence.restBetweenSets));
        calmNum.setText(Integer.toString(sequence.calm));

        Button btnBack = view.findViewById(R.id.btnBackFromUpdateSeq);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_editSeqFragment_to_itemFragment);
            }
        });
        Button btnSaveUpdatedSeq = (Button) view.findViewById(R.id.btnSaveUpdatedSeq);
        btnSaveUpdatedSeq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String name = appViewModel.getName(seqNameTextView.getText().toString());
                        int preparation = appViewModel.getNumberFromString(preparationNum.getText().toString());
                        int work = appViewModel.getNumberFromString(workNum.getText().toString());
                        int rest = appViewModel.getNumberFromString(restNum.getText().toString());
                        int cycles = appViewModel.getNumberFromString(cyclesNum.getText().toString());
                        int sets = appViewModel.getNumberFromString(setsNum.getText().toString());
                        int restBetweenSets = appViewModel.getNumberFromString(restBetweenSetsNum.getText().toString());
                        int calm = appViewModel.getNumberFromString(calmNum.getText().toString());

                        appViewModel.updateCurrentSequence(name, preparation, work, rest, cycles, sets, restBetweenSets, calm);

                        SequenceDatabase.getDatabase(getActivity()).timerDao().update(appViewModel.getCurrentSequence());
                        Navigation.findNavController(view).navigate(R.id.action_editSeqFragment_to_itemFragment);
                    }
                });
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarEdit);
        
        Button btnColor = view.findViewById(R.id.btnChooseColor);
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
                                        setSeqColor(envelope);
                                        toolbar.setBackgroundColor(envelope.getColor());
                                        btnSaveUpdatedSeq.setBackgroundColor(envelope.getColor());
                                        btnBack.setBackgroundColor(envelope.getColor());
                                        btnColor.setBackgroundColor(envelope.getColor());
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

        toolbar.setBackgroundColor(appViewModel.getCurrentSequence().colour);
        btnBack.setBackgroundColor(appViewModel.getCurrentSequence().colour);
        btnColor.setBackgroundColor(appViewModel.getCurrentSequence().colour);
        btnSaveUpdatedSeq.setBackgroundColor(appViewModel.getCurrentSequence().colour);

        return view;
    }

    public void setSeqColor(ColorEnvelope envelope) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Sequence sequence = appViewModel.getCurrentSequence();
                sequence.colour = envelope.getColor();
                SequenceDatabase.getDatabase(getActivity()).timerDao().update(sequence);
            }
        });
    }

}