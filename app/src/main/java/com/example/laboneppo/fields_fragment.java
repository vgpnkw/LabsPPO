package com.example.laboneppo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;


public class  fields_fragment extends Fragment {
    MainViewModel mainViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_fields_fragment, container, false);
        baseFunctionality(inflater, container, savedInstanceState, layout);
        return layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void baseFunctionality(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, View layout){
        Spinner inputSpinner = (Spinner) layout.findViewById(R.id.inputSpinner);
        setSpinner(layout, inputSpinner, Measure.values(), mainViewModel.getInputSpinner());

        final EditText edit = layout.findViewById(R.id.inputField);
        final TextView outputField = layout.findViewById(R.id.outputField);
        final Observer<String> inputObserver = new Observer<String>(){
            @Override
            public void onChanged(String newInput) {
                edit.setText(newInput);
                outputField.setText(mainViewModel.convert(newInput));
            }
        };

        final Observer<Measure> inputSpinnerObserver = new Observer<Measure>(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(Measure newInput) {
                Measure[] units = Arrays.stream(Measure.values())
                        .filter(value -> value.getCategory() == newInput.getCategory())
                        .toArray(Measure[]::new);
                Spinner outputSpinner = (Spinner)layout.findViewById(R.id.outputSpinner);
                setSpinner(layout, outputSpinner, units, mainViewModel.getOutputSpinner());
            }
        };

        final Observer<Measure> outputSpinnerObserver = new Observer<Measure>(){
            @Override
            public void onChanged(Measure newOutput){
                outputField.setText(mainViewModel.convert(newOutput));
            }
        };

        mainViewModel.getInputData().observe(getViewLifecycleOwner(), inputObserver);
        mainViewModel.getInputSpinner().observe(getViewLifecycleOwner(), inputSpinnerObserver);
        mainViewModel.getOutputSpinner().observe(getViewLifecycleOwner(), outputSpinnerObserver);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void setSpinner(View layout, Spinner spinner, Measure[] units, MutableLiveData<Measure> liveSpinner){
        String[] unitsSpinner = Arrays.stream(units).map(value -> value.name()).toArray(String[]::new);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item , unitsSpinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                Measure unit = Measure.valueOf(item);
                liveSpinner.setValue(unit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

}