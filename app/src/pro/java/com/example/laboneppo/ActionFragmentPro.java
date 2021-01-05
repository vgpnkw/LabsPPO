package com.example.laboneppo;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;

public class ActionFragmentFull extends ActionFragment {
    MainViewModel mainViewModel;
    ClipboardManager clipboardManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.action_fragment, container, false);


        clipboardManager = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        layout.findViewById(R.id.btnSwap).setOnClickListener(item -> mainViewModel.swapUnits());
        layout.findViewById(R.id.copyIn).setOnClickListener(item -> mainViewModel.copyInput(clipboardManager));
        layout.findViewById(R.id.copyOut).setOnClickListener(item -> mainViewModel.copyOutput(clipboardManager));
        return layout;
    }

}
