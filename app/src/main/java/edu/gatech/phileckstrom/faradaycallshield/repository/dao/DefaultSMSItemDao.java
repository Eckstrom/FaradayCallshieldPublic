package edu.gatech.phileckstrom.faradaycallshield.repository.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;

@Dao
public interface DefaultSMSItemDao {

    @Query("SELECT * FROM DefaultSMSItem ORDER BY locator DESC")
    LiveData<List<DefaultSMSItem>> getDateActivityEntries();

    @Query("SELECT * FROM DefaultSMSItem WHERE locator = :locator")
    LiveData<DefaultSMSItem> getDateActivityByDate(String locator);

    @Query("SELECT * FROM DefaultSMSItem WHERE locator = :locator")
    List<DefaultSMSItem> getMessagePresence(String locator);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(DefaultSMSItem activity);

    @Query("DELETE FROM DefaultSMSItem WHERE locator = :locator")
    void delete(String locator);

    @Query("UPDATE DefaultSMSItem SET message = :defaultMessage where locator = :locator")
    void updateMessage(String locator, String defaultMessage);

    @Query("UPDATE BlackListEntry SET sending_response = :isSending where phone_number = :locator")
    void updateIsSendingDefaultSMS(String locator, Integer isSending);
}
