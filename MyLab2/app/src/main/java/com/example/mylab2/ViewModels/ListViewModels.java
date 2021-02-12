package com.example.mylab2.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mylab2.DataBase.ActionModel;
import com.example.mylab2.DataBase.TimerModel;
import com.example.mylab2.App;


import java.util.List;

public class ListViewModels extends ViewModel
{
    private LiveData<List<TimerModel>> timers = App.getInstance().getTimerDao().GetAllAsLiveDatList();
    private LiveData<List<ActionModel>> actions = App.getInstance().getActionDao().GetAllAsLiveDatList();

    public LiveData<List<TimerModel>> getTimers()
    {
        return timers;
    }
    public LiveData<List<ActionModel>> getActions()
    {
        return actions;
    }

    public void setTimers(LiveData<List<TimerModel>> timers)
    {
        this.timers = timers;
    }
    public void setActions(LiveData<List<ActionModel>> actions)
    {
        this.actions = actions;
    }
}
