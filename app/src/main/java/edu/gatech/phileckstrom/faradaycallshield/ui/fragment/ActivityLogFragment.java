package edu.gatech.phileckstrom.faradaycallshield.ui.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;
import edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter.ActivityLogEntryAdapter;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

//If calling from fragment, use Childmanager, otherwise regular fragment manager.
//Dictates behavior for the Call Log fragment
public class ActivityLogFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public ListItemCollectionViewModel listItemCollectionViewModel;

    private RecyclerView recyclerView;

    private ActivityLogEntryAdapter adapter;

    //Unused constructor
    public ActivityLogFragment() {
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser){
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){
//            recyclerView.scrollToPosition(listOfData.size() - 1);
//        }
//    }

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

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);

        View view = inflater.inflate(R.layout.fragment_activitylog, container, false);
        recyclerView = view.findViewById(R.id.activityLogRecyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ActivityLogEntryAdapter();

        recyclerView.setAdapter(adapter);

        final Observer<List<ActivityLogItem>> nameObserver = new Observer<List<ActivityLogItem>>() {
            @Override
            public void onChanged(@Nullable final List<ActivityLogItem> entries1) {
                Executors.newCachedThreadPool()
                        .execute(new DataParser(adapter, entries1));
            }
        };

        listItemCollectionViewModel.getDateListItems().observe(this, nameObserver);

        return view;
    }

    private static class DataParser implements Runnable {

        private final WeakReference<ActivityLogEntryAdapter> weakRef;
        private final List<ActivityLogItem> entries1;

        public DataParser(ActivityLogEntryAdapter adapter, List<ActivityLogItem> entries1) {
            this.weakRef = new WeakReference<>(adapter);
            this.entries1 = entries1;
        }

        @Override
        public void run() {
            if (entries1 == null) {
                return;
            }

            final List<ActivityLogEntryAdapter.DataHolder> dataHolders = new ArrayList<>();
            HashMap<String, List<ActivityLogItem>> map = new LinkedHashMap<>();
            for (ActivityLogItem item : entries1) {
                if (!map.containsKey(item.date)) {
                    map.put(item.date, new ArrayList<ActivityLogItem>());
                }
                map.get(item.date).add(item);
            }

            for (Map.Entry<String, List<ActivityLogItem>> entry : map.entrySet()) {
                dataHolders.add(new ActivityLogEntryAdapter.HeaderDataHolder(entry.getKey()));
                for (ActivityLogItem i : entry.getValue()) {
                    dataHolders.add(new ActivityLogEntryAdapter.ActivityLogDataHolder(i));
                }
            }
            new Handler(Looper.getMainLooper())
                    .post(new Runnable() {
                        @Override
                        public void run() {
                            ActivityLogEntryAdapter adapter = weakRef.get();
                            if (adapter != null) {
                                adapter.submitList(dataHolders);
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private String getDate() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String dateToStr = format.format(today);
        System.out.println(dateToStr);

        return dateToStr;
    }
}


