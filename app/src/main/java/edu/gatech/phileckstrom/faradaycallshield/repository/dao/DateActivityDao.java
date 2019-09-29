package edu.gatech.phileckstrom.faradaycallshield.repository.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
