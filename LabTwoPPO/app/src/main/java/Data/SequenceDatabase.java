package Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.Sequence;

@Database(entities = {Sequence.class}, version = 1)
public abstract class SequenceDatabase extends RoomDatabase {

    public abstract SequenceDao timerDao();

    private static volatile SequenceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static SequenceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SequenceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SequenceDatabase.class, "sequenceDB")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
