package edu.gatech.phileckstrom.faradaycallshield.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistRepository;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;


public class ListItemCollectionViewModel extends ViewModel {

    private BlacklistRepository repository;

    ListItemCollectionViewModel(BlacklistRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<BlackListEntry>> getListItems() {
        return repository.getBlackListEntries();
    }

    public List<BlackListEntry> containsNumber(String input) {
        return repository.containsBlackListNumber(input);
    }

    public List<DefaultSMSItem> containsDefaultSMS(String input) {
        return repository.containsDefaultSMSItem(input);
    }

    public LiveData<List<ActivityLogItem>> getDateListItems() {
        return repository.getDateActivityEntries();
    }

    public void addNewItemToDatabase(BlackListEntry listItem){
        new ListItemCollectionViewModel.AddItemTask().execute(listItem);
    }

    public void deleteListItem(String listItem) {
        DeleteItemTask deleteItemTask = new DeleteItemTask();
        deleteItemTask.execute(listItem);
    }

    public void updateItemMessage(String listItem, String message) {
        UpdateItemTask updateItemTask = new UpdateItemTask();
        updateItemTask.execute(listItem, message);
    }

    public void updateBlockingCall(String number, boolean active)
    {
        UpdateCallBlockStatus updateCallBlockStatus = new UpdateCallBlockStatus();
        updateCallBlockStatus.execute(number, active);
    }

    public void updateBlockingText(String number, boolean active)
    {
        UpdateTextBlockStatus updateTextBlockStatus = new UpdateTextBlockStatus();
        updateTextBlockStatus.execute(number, active);
    }

    public void updateSendingText(String phoneNumber, boolean b) {
        UpdateIsSendingText updateIsSendingText = new UpdateIsSendingText();
        updateIsSendingText.execute(phoneNumber, b);
    }

    public void addNewDefaultSMSToDatabase(DefaultSMSItem defaultSMSResponse) {
        new ListItemCollectionViewModel.AddDefaultSMSItemTask().execute(defaultSMSResponse);
    }

    public void updateDefaultSMSMessage(String locator, String inputMessage) {

        UpdateDefaultSMSMessageTask updateItemTask = new UpdateDefaultSMSMessageTask();
        updateItemTask.execute(locator, inputMessage);
    }

    public void updateSendingDefaultSMS(String message, int sending) {
    }

    private class UpdateIsSendingDefaultSMS extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... item) {
            repository.getDefaultSMSItemDao().updateIsSendingDefaultSMS((String)item[0],(Integer)item[1]);
            return null;
        }
    }

    private class UpdateIsSendingText extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... item) {
            repository.getBlackListEntryDao().updateIsSendingText((String)item[0],(Boolean)item[1]);
            return null;
        }
    }

    private class UpdateCallBlockStatus extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... item) {
            repository.getBlackListEntryDao().updateIsBlockingCall((String)item[0],(Boolean)item[1]);
            return null;
        }
    }

    private class UpdateTextBlockStatus extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... item) {
            repository.getBlackListEntryDao().updateIsBlockingText((String)item[0],(Boolean)item[1]);
            return null;
        }
    }


    private class DeleteItemTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {
            repository.deleteBlackListItem(item[0]);
            return null;
        }
    }

    private class UpdateDefaultSMSMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {

            repository.updateDefaultSMSMessage(item[0], item[1]);
            return null;
        }
    }

    private class UpdateItemTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {

            repository.updateListItemMessage(item[0], item[1]);
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

    private class AddDefaultSMSItemTask extends AsyncTask<DefaultSMSItem, Void, Void> {

        @Override
        protected Void doInBackground(DefaultSMSItem... item) {
            repository.createNewDefaultSMSItem(item[0]);
            return null;
        }
    }

}

