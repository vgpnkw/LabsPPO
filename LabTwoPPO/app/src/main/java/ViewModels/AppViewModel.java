package ViewModels;

import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labtwoppo.R;

import java.util.ArrayList;
import java.util.List;

import Data.Timer;
import Models.Sequence;
import Models.SequenceInterval;
import Services.TimerService;

public class AppViewModel extends ViewModel {

    private MutableLiveData<Sequence> currentSequence;
    private MutableLiveData<Integer> timerValue;
    private List<SequenceInterval> intervals;
    private Timer timer;
    private int intervalIndex;
    private SoundPool soundPool;
    private int soundId;
    private Context context;

    public Sequence getCurrentSequence() {
        return currentSequence.getValue();
    }

    public void setCurrentSequence(Sequence sequence) {
        if (currentSequence == null) {
            currentSequence = new MutableLiveData<>();
        }
        currentSequence.setValue(sequence);
    }

    public void updateCurrentSequence(String name, int preparation, int work, int rest, int cycles,
                                 int sets, int restBetweenSets, int calm) {
        getCurrentSequence().name = name;
        getCurrentSequence().preparation = preparation;
        getCurrentSequence().work = work;
        getCurrentSequence().rest = rest;
        getCurrentSequence().cycles = cycles;
        getCurrentSequence().sets = sets;
        getCurrentSequence().restBetweenSets = restBetweenSets;
        getCurrentSequence().calm = calm;
    }

    public int getNumberFromString(String value) {
        if (value.equals("")) {
            return 1;
        }
        return Integer.parseInt(value);
    }

    public String getName(String value) {
        if (value.equals("")) {
            return String.valueOf(R.string.unnamed);
        }
        return value;
    }

    public LiveData<Integer> getTimerValue() {
        if (timerValue == null) {
            timerValue = new MutableLiveData<>(0);
        }
        return timerValue;
    }

    public void setTimerValue(int millisUntilFinished) {
        if (timerValue == null) {
            timerValue = new MutableLiveData<>();
        }
        timerValue.setValue(millisUntilFinished);
    }

    public void timerStart() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (intervalIndex < intervals.size()) {
            timer = new Timer((long)timerValue.getValue() * 1000, (long) 1000, this);
            timer.start();
        }
    }

    public void timerStop(boolean stop) {
        if (timer != null) {
            timer.cancel();
            if (stop) {
                setTimerValue(intervals.get(0).value);
                timer = null;
            }
            Intent intent = new Intent(context, TimerService.class);
            context.stopService(intent);
        }
    }

    public void timerPrev() {
        if (intervalIndex > 0) {
            intervalIndex--;
            setTimerValue(intervals.get(intervalIndex).value);
            timerStop(false);
            timerStart();
        }
    }

    public void timerNext() {
        if (intervalIndex + 1 < intervals.size()) {
            intervalIndex++;
            setTimerValue(intervals.get(intervalIndex).value);
            timerStop(false);
            timerStart();
        }
    }

    public void setIntervals() {
        intervals = new ArrayList<>();
        Sequence sequence = getCurrentSequence();
        intervals.add(new SequenceInterval(context.getResources().getString(R.string.preparation), sequence.preparation));

        for (int i = 0; i < sequence.sets; i++) {
            for (int j = 0; j < sequence.cycles - 1; j++) {
                intervals.add(new SequenceInterval(context.getResources().getString(R.string.work), sequence.work));
                intervals.add(new SequenceInterval(context.getResources().getString(R.string.rest), sequence.rest));
            }
            intervals.add(new SequenceInterval(context.getResources().getString(R.string.work), sequence.work));
            if (i + 1 != sequence.sets) {
                intervals.add(new SequenceInterval(context.getResources().getString(R.string.rest), sequence.restBetweenSets));
            }
        }

        intervals.add(new SequenceInterval(context.getResources().getString(R.string.calm), sequence.calm));
    }

    public List<SequenceInterval> getIntervals() {
        return intervals;
    }

    public int getIntervalIndex() {
        return intervalIndex;
    }

    public void setIntervalIndex(int intervalIndex) {
        this.intervalIndex = intervalIndex;
    }

    public void playSound() {
        soundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public void setSoundPool(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    public void startTimerService() {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra("timerValue", getTimerValue().getValue());

        ArrayList<Integer> curIntervals = new ArrayList<>();
        for (SequenceInterval interval: getIntervals()) {
            curIntervals.add(interval.value);
        }

        intent.putIntegerArrayListExtra("intervals", curIntervals);
        intent.putExtra("intervalIndex", getIntervalIndex());
        context.startService(intent);
        timerStart();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}