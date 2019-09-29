package edu.gatech.phileckstrom.faradaycallshield.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistRepository;



public class NewListItemViewModel extends ViewModel {

    private BlacklistRepository repository;

    NewListItemViewModel(BlacklistRepository repository) {
        this.repository = repository;
    }

    public List<BlackListEntry> containsNumber(String input) {
        return repository.containsBlackListNumber(input);
    }

    public BlacklistRepository getRepository() {
        return repository;
    }

    public void addNewItemToDatabase(BlackListEntry listItem){
        new AddItemTask().execute(listItem);
    }

    private class ComtainsItemTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {
            repository.containsBlackListNumber(item[0]);
            return null;
        }
    }

    private class AddItemTask extends AsyncTask<BlackListEntry, Void, Void> {

        @Override
        protected Void doInBackground(BlackListEntry... item) {
            repository.createNewBlackListItem(item[0]);
            return null;
        }
    }
}
