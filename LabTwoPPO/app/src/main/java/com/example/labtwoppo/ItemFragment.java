package com.example.labtwoppo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Data.SequenceDatabase;
import Helpers.TouchHelper;
import Models.Sequence;
import ViewModels.AppViewModel;


public class ItemFragment extends Fragment {
    private static final String COUNT = "column-count";
    private int anInt = 1;
    private List<Sequence> sequences;
    AppViewModel appViewModel;
    RecyclerView recyclerView;
    ItemAdapter adapter;





    private void initSequenceList() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sequences = SequenceDatabase.getDatabase(requireActivity()).timerDao().getAll();
            }
        });
    }

    public ItemFragment() {
    }




    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            anInt = getArguments().getInt(COUNT);
        }

        initSequenceList(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.setContext(requireActivity());

        Button btnAddSeq = view.findViewById(R.id.btnAddSeq);
        btnAddSeq.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_sequenceFragment);
        });
        Button btnSettings = view.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_itemFragment_to_settingsFragment);
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();
        if (anInt <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, anInt));
        }
        adapter = new ItemAdapter(sequences, context, appViewModel);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new TouchHelper(adapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        return view;
    }
}