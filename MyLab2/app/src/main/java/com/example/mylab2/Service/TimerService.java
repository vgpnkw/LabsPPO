package com.example.mylab2.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import com.example.mylab2.App;
import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.Enums.Status;
import com.example.mylab2.R;

import java.util.List;

public class TimerService extends Service
{
    public List<ActionModel> actions;
    public  TimerModel timer;

    public MediaPlayer mediaPlayer;
    public CountDownTimer countDownTimer;

    public int actionNumber = 0;
    public Status status = Status.WAITING;
    public int balance;
    public MyBinder binder = new MyBinder();

    public void setActionNumber(int actionNumber)
    {
        this.actionNumber = actionNumber;
        ActionModel currentAction = actions.get(actionNumber);
        StartTimer(currentAction.SecondsNumber, currentAction.Name);
    }



    @Override
    public void onStart(Intent intent, int startid)
    {
        timer = App.getInstance().getTimerDao().Get(intent.getExtras().getInt("timerId", -1));
        actions = App.getInstance().getActionDao().GetAllByTimerId(timer.Id);
        if(actions.size() != 0)
        {
            ActionModel currentAction = actions.get(actionNumber);
            status = Status.STARTED;
            StartTimer(currentAction.SecondsNumber, currentAction.Name);

        }
        else
        {
            mediaPlayer = (MediaPlayer.create(getApplicationContext(), R.raw.finish));
            Toast.makeText(getApplicationContext(), timer.Name, Toast.LENGTH_LONG).show();
            mediaPlayer.start();
        }
    }

    private void StartTimer(int secondsNumber, String actionName)
    {
        if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(secondsNumber * 1000, 1000)
        {
            @Override
            public void onFinish()
            {
                if (actionNumber < actions.size() - 1)
                {
                    Toast.makeText(getApplicationContext(), actionName, Toast.LENGTH_SHORT).show();
                    setActionNumber(++actionNumber);
                }
                else
                 {
                     mediaPlayer = (MediaPlayer.create(getApplicationContext(), R.raw.finish));
                    Toast.makeText(getApplicationContext(), timer.Name, Toast.LENGTH_LONG).show();
                }
                mediaPlayer.start();
            }

            @Override
            public void onTick(long time)
            {
                balance = (int)(time/1000);
                Intent intent = new Intent("TIMER_ACTION");
                intent.putExtra("currentTime", balance);
                intent.putExtra("actionName", actionName);
                sendBroadcast(intent);
            }
        };
        countDownTimer.start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate()
    {
        mediaPlayer = MediaPlayer.create(this, R.raw.start);
    }

    @Override
    public void onDestroy()
    {
        mediaPlayer.stop();
    }



    public void PauseTimer()
    {
        if (status == Status.STARTED)
        {
            if(countDownTimer != null)
            {
                countDownTimer.cancel();
                status = Status.STOPPED;
            }
        }
        else
        {
            ActionModel currentAction = actions.get(actionNumber);
            StartTimer(balance, currentAction.Name);
            status = Status.STARTED;
        }
    }

    public void ResetTimer()
    {
        setActionNumber(0);
    }

    public void PrevAction()
    {
        if (status == Status.STARTED)
        {
            if(actionNumber > 0)
            {
                setActionNumber(--actionNumber);
            }
        }
    }

    public void NextAction()
    {
        if (status == Status.STARTED)
        {
            if(actionNumber < actions.size() - 1)
            {
                setActionNumber(++actionNumber);
            }
        }
    }

    public class MyBinder extends Binder
    {
        public TimerService getService()
        {
            return TimerService.this;
        }
    }
}

