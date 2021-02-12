package com.example.mylab2.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mylab2.Dao.ActionDao;
import com.example.mylab2.Dao.TimerDao;

@Database(entities = {ActionModel.class, TimerModel.class}, version = 1, exportSchema = false)
public abstract class DbHelper extends RoomDatabase
{
    public abstract TimerDao timerDao();
    public abstract ActionDao actionDao();
}
