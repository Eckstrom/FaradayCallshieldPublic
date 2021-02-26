package edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter;

import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

//Custom recyclerview adapter class
public class BlackListEntryAdapter extends ListAdapter<BlackListEntry, BlackListEntryAdapter.BlackListViewHolder> {

    public final ListItemCollectionViewModel listItemCollectionViewModel;

    public BlackListEntryAdapter(ListItemCollectionViewModel viewModel) {
        super(new DiffUtil.ItemCallback<BlackListEntry>() {
            @Override
            public boolean areItemsTheSame(BlackListEntry oldItem, BlackListEntry newItem) {
                return  oldItem != null && newItem != null && oldItem.phoneNumber.equals(newItem.phoneNumber);
            }

            @Override
            public boolean areContentsTheSame(BlackListEntry oldItem, BlackListEntry newItem) {
                return oldItem != null && oldItem.equals(newItem);
            }
        });

        this.listItemCollectionViewModel = viewModel;
    }

    @NonNull
    @Override
    public BlackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blacklist, parent, false);
        return new BlackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListViewHolder holder, int position) {

        final BlackListEntry currentItem = getItem(position);

        //setup switches
        holder.callSwitch.setOnCheckedChangeListener(null);
        holder.callSwitch.setChecked(currentItem.isBlockingCall);
        holder.callSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listItemCollectionViewModel.updateBlockingCall(currentItem.phoneNumber, b);
            }
        });

        holder.textSwitch.setOnCheckedChangeListener(null);
        holder.textSwitch.setChecked(currentItem.isBlockingSMS);
        holder.entryNumber.setText(Integer.toString(position + 1));

        holder.textSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listItemCollectionViewModel.updateBlockingText(currentItem.phoneNumber, b);
            }
        });

        holder.title.setText(currentItem.phoneNumber);
        holder.descriptor.setText(currentItem.contactName);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final LayoutInflater li = LayoutInflater.from(v.getContext());
                View confirmationsView = li.inflate(R.layout.confirmations, null);

                //Initialize AlertDialogBuilder and pass confirmations view reference to it
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setView(confirmationsView);

                //Set alert buttons:
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listItemCollectionViewModel.deleteListItem(currentItem.phoneNumber);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // Create and show AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    class BlackListViewHolder extends RecyclerView.ViewHolder {
        TextView entryNumber;
        TextView title;
        TextView descriptor;
        Switch callSwitch;
        Switch textSwitch;

        BlackListViewHolder(View itemView) {
            super(itemView);
            entryNumber = itemView.findViewById(R.id.item_blacklist_number);
            descriptor = itemView.findViewById(R.id.entryTitle);
            title = itemView.findViewById(R.id.entryDescriptor);
            callSwitch = itemView.findViewById(R.id.block_call_switch);
            textSwitch = itemView.findViewById(R.id.block_text_switch);
        }
    }
}
