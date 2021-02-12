package com.example.mylab2.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mylab2.Enums.Status;

public class TimerViewModel extends ViewModel
{
    private MutableLiveData<Integer> time = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> position = new MutableLiveData<Integer>();
    private Status actionStatus = Status.WAITING;


    public void setTime(int time)
    {
        this.time.setValue(time);
    }

    public void setPosition(int position)
    {
        this.position.setValue(position);
    }

    public Status getActionStatus()
    {
        return actionStatus;
    }

    public void setStatus(Status status) {
        this.actionStatus = status;
    }

    public LiveData<Integer> getPosition()
    {
        return position;
    }

    public LiveData<Integer> getTime()
    {
        return time;
    }

    public boolean IsNotActionStart()
    {
        if(actionStatus != Status.STARTED)
        {
            return false;
        }
        else 
        {
            return true;
        }
    }


}
