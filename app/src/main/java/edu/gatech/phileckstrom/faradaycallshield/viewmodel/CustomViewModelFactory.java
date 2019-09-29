package edu.gatech.phileckstrom.faradaycallshield.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistRepository;



@Singleton
public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private final BlacklistRepository repository;

    @Inject
    public CustomViewModelFactory(BlacklistRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListItemCollectionViewModel.class))
            return (T) new ListItemCollectionViewModel(repository);

        else if (modelClass.isAssignableFrom(NewListItemViewModel.class))
            return (T) new NewListItemViewModel(repository);

        else if (modelClass.isAssignableFrom(UpdateMessageViewModel.class))
            return (T) new UpdateMessageViewModel(repository);

        else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
