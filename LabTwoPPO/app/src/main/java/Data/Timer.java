package Data;

import android.os.CountDownTimer;

import ViewModels.AppViewModel;

public class Timer extends CountDownTimer {
    AppViewModel appViewModel;

    public Timer(Long millisInFuture, Long countDownInterval, AppViewModel appViewModel) {
        super(millisInFuture, countDownInterval);
        this.appViewModel = appViewModel;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        appViewModel.setTimerValue((int) millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {
        appViewModel.setIntervalIndex(appViewModel.getIntervalIndex() + 1);
        appViewModel.playSound();
        if (appViewModel.getIntervalIndex() < appViewModel.getIntervals().size()) {
            appViewModel.setTimerValue(appViewModel.getIntervals().get(appViewModel.getIntervalIndex()).value);
            appViewModel.timerStart();
        }
    }


}