package edu.gatech.phileckstrom.faradaycallshield.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.repository.dao.BlackListEntryDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DateActivityDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DefaultSMSItemDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;


public class BlacklistRepository {

    public final BlackListEntryDao blackListEntryDao;
    public final DateActivityDao dateActivityDao;
    public final DefaultSMSItemDao defaultSMSItemDao;

    @Inject
    public BlacklistRepository(BlackListEntryDao blackListEntryDao, DateActivityDao dateActivityDao, DefaultSMSItemDao defaultSMSItemDao) {
        this.blackListEntryDao = blackListEntryDao;
        this.dateActivityDao = dateActivityDao;
        this.defaultSMSItemDao = defaultSMSItemDao;
    }

    //Blacklist DAO functions

    public LiveData<List<BlackListEntry>> getBlackListEntries(){
        return blackListEntryDao.getBlackListEntries();
    }

    public LiveData<BlackListEntry> getBlackListItem(String itemId){
        return blackListEntryDao.getBlackListEntryById(itemId);
    }

    public Long createNewBlackListItem(BlackListEntry entry){
        return blackListEntryDao.insert(entry);
    }

    public void deleteBlackListItem(String entry){
        blackListEntryDao.delete(entry);
    }

    public void updateListItemMessage(String entry, String message) {
        blackListEntryDao.updateMessage(entry, message);
    }

    public List<BlackListEntry> containsBlackListNumber(String input){
        return blackListEntryDao.getNumberMatches(input);
    }

    public BlackListEntryDao getBlackListEntryDao()
    {
        return this.blackListEntryDao;
    }


    //Date Activity DAO

    public LiveData<List<ActivityLogItem>> getDateActivityEntries(){
        return dateActivityDao.getDateActivityEntries();
    }

    public LiveData<ActivityLogItem> getDateActivityItem(String itemId){
        return dateActivityDao.getDateActivityByDate(itemId);
    }

    public Long createNewDateActivityItem(ActivityLogItem entry){
        return dateActivityDao.insert(entry);
    }

    public void deleteDateActivityItem(String entry){
        dateActivityDao.delete(entry);
    }


    public DateActivityDao getDateActivityEntryDao()
    {
        return this.dateActivityDao;
    }


    //Default SMS DAO

    //Date Activity DAO

    public LiveData<List<DefaultSMSItem>> getDefaultSMSItemEntries(){
        return defaultSMSItemDao.getDateActivityEntries();
    }

    public LiveData<DefaultSMSItem> getDefaultSMSItem(String itemId){
        return defaultSMSItemDao.getDateActivityByDate(itemId);
    }

    public void updateDefaultSMSMessage(String entry, String message) {
        defaultSMSItemDao.updateMessage(entry, message);
    }

    public Long createNewDefaultSMSItem(DefaultSMSItem entry){
        return defaultSMSItemDao.insert(entry);
    }

    public void deleteDefaultSMSItem(String entry){
        defaultSMSItemDao.delete(entry);
    }


    public DefaultSMSItemDao getDefaultSMSItemDao()
    {
        return this.defaultSMSItemDao;
    }

    public List<DefaultSMSItem> containsDefaultSMSItem(String input){
        return defaultSMSItemDao.getMessagePresence(input);
    }
}
