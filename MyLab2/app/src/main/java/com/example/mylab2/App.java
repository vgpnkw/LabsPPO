package com.example.mylab2;

import android.app.Application;

import androidx.room.Room;

import com.example.mylab2.DataBase.DbHelper;
import com.example.mylab2.Dao.ActionDao;
import com.example.mylab2.Dao.TimerDao;


public class App extends Application {
    private DbHelper database;
    private TimerDao timerDao;
    private ActionDao actionDao;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        database = Room.databaseBuilder(getApplicationContext(), DbHelper.class, "timerDb").allowMainThreadQueries().build();
        timerDao = database.timerDao();
        actionDao = database.actionDao();
    }

    public DbHelper getDatabase() {
        return database;
    }

    public void setDatabase(DbHelper database) {
        this.database = database;
    }

    public TimerDao getTimerDao() {
        return timerDao;
    }

    public void setTimerDao(TimerDao timerDao) {
        this.timerDao = timerDao;
    }

    public ActionDao getActionDao() {
        return actionDao;
    }

    public void setActionDao(ActionDao actionDao) {
        this.actionDao = actionDao;
    }
}
