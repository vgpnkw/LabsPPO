package com.example.mylab2.DataBase;

import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

import androidx.room.Ignore;

import com.example.mylab2.DataBase.TimerModel;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "actions", foreignKeys = @ForeignKey(entity = TimerModel.class, parentColumns = "id", childColumns = "timerId", onDelete = CASCADE))
public class ActionModel
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String Name;
    public int SecondsNumber = 10;
    public int TimerId;

    @Ignore
    public ActionModel(String name, int secondsNumber, int timerId)
    {
        this.Name = name;
        this.SecondsNumber = secondsNumber;
        this.TimerId = timerId;
    }

    @Ignore
    public ActionModel(ActionModel action)
    {
        this.Name = action.Name;
        this.SecondsNumber = action.SecondsNumber;
        this.TimerId = action.TimerId;
    }
}