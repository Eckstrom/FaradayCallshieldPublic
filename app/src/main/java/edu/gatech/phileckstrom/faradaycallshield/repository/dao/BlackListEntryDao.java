package edu.gatech.phileckstrom.faradaycallshield.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;

@Dao
public interface BlackListEntryDao {

    @Query("SELECT * FROM BlackListEntry")
    LiveData<List<BlackListEntry>> getBlackListEntries();

    @Query("SELECT * FROM BlackListEntry WHERE phone_number = :index")
    LiveData<BlackListEntry> getBlackListEntryById(String index);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insert(BlackListEntry entry);

    @Query("DELETE FROM BlackListEntry WHERE phone_number = :index")
    void delete(String index);


    @Query("UPDATE BlacklistEntry SET reply_message = :newMessage where phone_number = :index")
    void updateMessage(String index, String newMessage);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(BlackListEntry... blackListEntries);

    @Query("SELECT * FROM BlacklistEntry WHERE phone_number = :number AND blocking_call = 1")
    List<BlackListEntry> getNumberMatches(String number);

    @Query("SELECT * FROM BlacklistEntry")
    List<BlackListEntry> getAllEntries();

    @Query("SELECT * FROM BlacklistEntry WHERE phone_number LIKE '%#%' AND blocking_call = 1")
    List<BlackListEntry> getNumberMatchesWithPound();

    @Query("UPDATE BlackListEntry SET blocking_call = :isBlocked where phone_number = :number")
    void updateIsBlockingCall(String number, boolean isBlocked);

    @Query("UPDATE BlackListEntry SET blocking_sms = :isBlocked where phone_number = :number")
    void updateIsBlockingText(String number, boolean isBlocked);

    @Query("UPDATE BlackListEntry SET sending_response = :isSending where phone_number = :number")
    void updateIsSendingText(String number, boolean isSending);

}
