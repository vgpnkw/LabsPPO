package Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

public class TimerService extends Service {

    private static final int MAX_STREAMS = 1;
    private long timerValue;
    private List<Integer> intervalValues;
    private int intervalIndex;
    private CountDownTimer timer;
    private SoundPool soundPool;
    private int soundId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timerValue = intent.getIntExtra("timerValue", 0);
        intervalIndex = intent.getIntExtra("intervalIndex", 0);
        intervalValues = intent.getIntegerArrayListExtra("intervals");

        soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        try {
            soundId = soundPool.load(getAssets().openFd("sound1.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        startTimer();

        return START_STICKY;
    }

    private void startTimer() {
        timer = new CountDownTimer(timerValue * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinish) {
                timerValue = millisUntilFinish;
            }

            @Override
            public void onFinish() {
                soundPool.play(soundId,1, 1, 0, 0, 1);
                intervalIndex++;
                if (intervalIndex < intervalValues.size()) {
                    timerValue = intervalValues.get(intervalIndex);
                    startTimer();
                }
            }
        };
        timer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        timer.cancel();

        super.onDestroy();
    }
}