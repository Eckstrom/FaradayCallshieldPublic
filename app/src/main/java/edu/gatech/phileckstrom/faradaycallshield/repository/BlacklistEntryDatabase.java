package edu.gatech.phileckstrom.faradaycallshield.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import edu.gatech.phileckstrom.faradaycallshield.repository.dao.BlackListEntryDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DateActivityDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DefaultSMSItemDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;

@Database(entities = {BlackListEntry.class, ActivityLogItem.class, DefaultSMSItem.class}, version = 3, exportSchema = false)
public abstract class BlacklistEntryDatabase extends RoomDatabase {

    public abstract BlackListEntryDao blackListEntryDao();
    public abstract DateActivityDao dateActivityDao();
    public abstract DefaultSMSItemDao defaultSMSItemDao();

}
