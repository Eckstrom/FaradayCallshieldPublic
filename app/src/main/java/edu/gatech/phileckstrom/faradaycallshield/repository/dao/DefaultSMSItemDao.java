package edu.gatech.phileckstrom.faradaycallshield.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;

//Database command object for default SMS to be sent to blocked numbers
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
