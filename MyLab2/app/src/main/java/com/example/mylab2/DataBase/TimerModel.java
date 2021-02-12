package com.example.mylab2.DataBase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TimerModel
{
    @PrimaryKey(autoGenerate = true)
    public int Id;
    public String Name;
    public String Description;
    public String CreatedDate;
    public int Color;

    @Ignore
    public TimerModel(String name, String description, String createdDate, int color)
    {
        this.Name = name;
        this.Description = description;
        this.CreatedDate = createdDate;
        this.Color = color;
    }

    @Ignore
    public TimerModel(TimerModel timer)
    {
        this.Name = timer.Name;
        this.Description = timer.Description;
        this.Color = timer.Color;
    }

    public TimerModel() {

    }
}
