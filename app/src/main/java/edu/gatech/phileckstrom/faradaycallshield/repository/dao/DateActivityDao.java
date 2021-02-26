package edu.gatech.phileckstrom.faradaycallshield.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;

@Dao
public interface DateActivityDao {

    @Query("SELECT * FROM ActivityLogItem ORDER BY date DESC, time DESC")
    LiveData<List<ActivityLogItem>> getDateActivityEntries();

    @Query("SELECT * FROM ActivityLogItem WHERE date = :index")
    LiveData<ActivityLogItem> getDateActivityByDate(String index);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(ActivityLogItem activity);

    @Query("DELETE FROM ActivityLogItem WHERE id = :id")
    void delete(String id);
}
