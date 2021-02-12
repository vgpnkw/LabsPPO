package com.example.mylab2.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.mylab2.DataBase.TimerModel;

import java.util.List;

@Dao
public interface TimerDao
{
    @Query("SELECT * FROM timermodel WHERE id LIKE :id LIMIT 1")
    TimerModel Get(int id);

    @Query("SELECT * FROM timermodel")
    List<TimerModel> GetAll();

    @Query("SELECT * FROM timermodel")
    LiveData<List<TimerModel>> GetAllAsLiveDatList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(TimerModel timer);

    @Update
    void Update(TimerModel timer);

    @Delete
    void Delete(TimerModel timer);

    @Query("DELETE  FROM timermodel")
    void DeleteAll();
}