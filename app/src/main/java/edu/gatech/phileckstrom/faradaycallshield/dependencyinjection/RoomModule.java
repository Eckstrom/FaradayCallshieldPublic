
package edu.gatech.phileckstrom.faradaycallshield.dependencyinjection;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.BlackListEntryDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistEntryDatabase;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistRepository;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DateActivityDao;
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DefaultSMSItemDao;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.CustomViewModelFactory;

//Adding migration:
//.addMigrations(new Migration(1,2) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//        }
//        })

@Module
public class RoomModule {

    private final BlacklistEntryDatabase database;

    public RoomModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                BlacklistEntryDatabase.class,
                "ListItem.db"
        ).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).allowMainThreadQueries().build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS ActivityLogItem (id INTEGER primary key autoincrement NOT NULL, date TEXT, time TEXT, caller TEXT)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS DefaultSMSItem (id INTEGER primary key autoincrement NOT NULL, locator TEXT, message TEXT, sending INTEGER NOT NULL)");
        }
    };

//    static final Migration MIGRATION_1_3 = new Migration(1, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE IF NOT EXISTS ActivityLogItem (id INTEGER primary key autoincrement NOT NULL, date TEXT, time TEXT, caller TEXT)");
//            database.execSQL("CREATE TABLE IF NOT EXISTS DefaultSMSItem (id INTEGER primary key autoincrement NOT NULL, locator TEXT, message TEXT, sending INTEGER NOT NULL)");
//        }
//    };

    @Provides
    @Singleton
    BlacklistRepository provideListItemRepository(BlackListEntryDao blackListEntryDao, DateActivityDao dateActivityDao, DefaultSMSItemDao defaultSMSItemDao){
        return new BlacklistRepository(blackListEntryDao, dateActivityDao, defaultSMSItemDao);
    }

    @Provides
    @Singleton
    BlackListEntryDao provideListItemDao(BlacklistEntryDatabase database){
        return database.blackListEntryDao();
    }

    @Provides
    @Singleton
    DateActivityDao provideDateActivityDao(BlacklistEntryDatabase database){
        return database.dateActivityDao();
    }

    @Provides
    @Singleton
    DefaultSMSItemDao provideDefaultSMSItemDao(BlacklistEntryDatabase database){
        return database.defaultSMSItemDao();
    }

    @Provides
    @Singleton
    BlacklistEntryDatabase provideListItemDatabase(Application application){
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(BlacklistRepository repository){
        return new CustomViewModelFactory(repository);
    }
}
