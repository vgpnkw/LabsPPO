package com.example.mylab2.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;


import com.example.mylab2.DataBase.ActionModel;

import java.util.List;

@Dao
public interface ActionDao
{
    @Query("SELECT * FROM ACTIONS WHERE id LIKE :id LIMIT 1")
    ActionModel Get(int id);

    @Query("SELECT * FROM ACTIONS")
    List<ActionModel> GetAll();

    @Query("SELECT * FROM ACTIONS")
    LiveData<List<ActionModel>> GetAllAsLiveDatList();

    @Query("SELECT * FROM ACTIONS WHERE timerId = :timerId")
    List<ActionModel> GetAllByTimerId(int timerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(ActionModel action);

    @Update
    void Update(ActionModel action);

    @Delete
    void Delete(ActionModel action);

    @Query("DELETE  FROM ACTIONS WHERE timerId = :timerId")
    void DeleteAllByTimerId(int timerId);
}
