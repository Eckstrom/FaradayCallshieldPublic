package edu.gatech.phileckstrom.faradaycallshield.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistRepository;



public class UpdateMessageViewModel extends ViewModel {

    private BlacklistRepository repository;

    UpdateMessageViewModel(BlacklistRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<BlackListEntry>> getMessageListItems() {
        return repository.getBlackListEntries();
    }

    public void updateItemMessage(String listItem, String message) {
        UpdateItemTask deleteItemTask = new UpdateItemTask();
        deleteItemTask.execute(listItem, message);
    }

    private class UpdateItemTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {

            repository.updateListItemMessage(item[0], item[1]);
            return null;
        }
    }

}


