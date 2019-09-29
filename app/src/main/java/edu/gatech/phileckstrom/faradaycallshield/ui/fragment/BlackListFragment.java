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
import android.widget.ImageButton;

import java.util.List;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.ui.dialog.AddBlacklistEntryDialogFragment;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter.BlackListEntryAdapter;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

//If calling from fragment, use Childmanager, otherwise regular fragment manager.


public class BlackListFragment extends Fragment implements View.OnClickListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public ListItemCollectionViewModel listItemCollectionViewModel;

    ImageButton addEntryBtn;

    public BlackListFragment() {
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

        final BlackListEntryAdapter adapter = new BlackListEntryAdapter(listItemCollectionViewModel);
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.blacklistRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        addEntryBtn = view.findViewById(R.id.addButton);
        addEntryBtn.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final Observer<List<BlackListEntry>> nameObserver = new Observer<List<BlackListEntry>>() {
            @Override
            public void onChanged(@Nullable final List<BlackListEntry> entries1) {

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

    @Override
    public void onClick(View view) {
        AddBlacklistEntryDialogFragment sheet = new AddBlacklistEntryDialogFragment();
        sheet.show(getChildFragmentManager(), "onClick");
    }
}


