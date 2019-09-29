package edu.gatech.phileckstrom.faradaycallshield.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter.MessageEntryAdapter;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

import java.util.List;

import javax.inject.Inject;


public class MessageFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ListItemCollectionViewModel listItemCollectionViewModel;

    private RecyclerView recyclerView;

    private List<BlackListEntry> listOfData;


    //Unused constructor
    public MessageFragment() {
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((FaradayCallshieldApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //Set up and subscribe (observe) to the ViewModel
        listItemCollectionViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ListItemCollectionViewModel.class);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.messageRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        final MessageEntryAdapter adapter = new MessageEntryAdapter(listItemCollectionViewModel, getActivity());
        recyclerView.setAdapter(adapter);

        final Observer<List<BlackListEntry>> nameObserver = new Observer<List<BlackListEntry>>() {
            @Override
            public void onChanged(@Nullable final List<BlackListEntry> entries1) {
                // Update the UI, in this case, a TextView.
                adapter.submitList(entries1);
            }
        };

        listItemCollectionViewModel.getListItems().observe(this, nameObserver);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}



