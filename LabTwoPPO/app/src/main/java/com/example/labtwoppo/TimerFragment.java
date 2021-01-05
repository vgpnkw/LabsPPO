package com.example.labtwoppo;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

import Models.Sequence;
import ViewModels.AppViewModel;

public class TimerFragment extends Fragment implements SoundPool.OnLoadCompleteListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int MAX_STREAMS = 1;
    private int mColumnCount = 1;
    private AppViewModel appViewModel;
    private int soundId;
    
    public TimerFragment() {
    }

    public static TimerFragment newInstance(int columnCount) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        SoundPool soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);

        soundId = 0;
        try {
            soundId = soundPool.load(requireActivity().getAssets().openFd("sound1.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        appViewModel.setSoundPool(soundPool);
        appViewModel.setSoundId(soundId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_list, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        int fontSize = sharedPreferences.getInt("fontSize", 1);
        FontChangeSize.changeFontSizeInViews(view, fontSize);

        appViewModel.setIntervals();
        appViewModel.timerStop(true);
        appViewModel.setIntervalIndex(0);
        appViewModel.setTimerValue(appViewModel.getIntervals().get(0).value);
        appViewModel.setContext(requireActivity());

        TextView timerValueTextView = (TextView) view.findViewById(R.id.timerTextView);

        view.findViewById(R.id.btnStart).setOnClickListener(v -> {
            /*Intent intent = new Intent(requireActivity(), TimerService.class);
            intent.putExtra("timerValue", appViewModel.getTimerValue().getValue());
            ArrayList<Integer> intervals = new ArrayList<>();
            for (SequenceInterval interval: appViewModel.getIntervals()) {
                intervals.add(interval.value);
            }
            intent.putIntegerArrayListExtra("intervals", intervals);
            intent.putExtra("intervalIndex", appViewModel.getIntervalIndex());
            intent.putExtra("sound", soundId);
            requireActivity().startService(intent);*/
            appViewModel.startTimerService();
            //appViewModel.timerStart();
            });

        view.findViewById(R.id.btnPause).setOnClickListener(v -> { appViewModel.timerStop(false); });
        view.findViewById(R.id.btnStop).setOnClickListener(v -> {
            appViewModel.timerStop(true);
            Navigation.findNavController(view).navigate(R.id.action_timerFragment_to_itemFragment);
        });
        view.findViewById(R.id.btnPrev).setOnClickListener(v -> { appViewModel.timerPrev(); });
        view.findViewById(R.id.btnNext).setOnClickListener(v -> { appViewModel.timerNext(); });

        appViewModel.getTimerValue().observe(getViewLifecycleOwner(), timerValue -> {
                timerValueTextView.setText(String.valueOf(timerValue));
        });

        Sequence sequence = appViewModel.getCurrentSequence();
        TextView seqNameTextView = (TextView) view.findViewById(R.id.seqNameInTimerListTextView);
        seqNameTextView.setText(appViewModel.getCurrentSequence().name);
        seqNameTextView.setBackgroundColor(sequence.colour);
        view.findViewById(R.id.toolbarTimerList).setBackgroundColor(sequence.colour);
        view.findViewById(R.id.linearLayoutTimer).setBackgroundColor(sequence.colour);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listTimer);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new TimerAdapter(appViewModel, appViewModel.getIntervals(), sequence.colour, requireActivity()));

        return view;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {

    }
}