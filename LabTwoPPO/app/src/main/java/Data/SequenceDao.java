package Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Models.Sequence;

@Dao
public interface SequenceDao {

    @Query("SELECT * FROM Sequence")
    List<Sequence> getAll();

    @Query("SELECT * FROM Sequence WHERE id IN (:seqIds)")
    List<Sequence> loadAllByIds(int[] seqIds);

    @Query("SELECT * FROM Sequence WHERE name LIKE :name LIMIT 1")
    Sequence findByName(String name);

    @Insert
    void insert(Sequence sequence);

    @Delete
    void delete(Sequence sequence);

    @Update
    void update(Sequence sequence);

    @Query("DELETE FROM Sequence")
    void deleteAll();
}
