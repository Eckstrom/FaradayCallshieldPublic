package edu.gatech.phileckstrom.faradaycallshield.ui.fragment.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

//Custom recyclerview adapter class
public class MessageEntryAdapter extends ListAdapter<BlackListEntry, MessageEntryAdapter.MessageListViewHolder> {

    private final ListItemCollectionViewModel listItemCollectionViewModel;
    private final Context context;


    public MessageEntryAdapter(ListItemCollectionViewModel viewModel, Context ctx) {

        super(new DiffUtil.ItemCallback<BlackListEntry>() {
            @Override
            public boolean areItemsTheSame(BlackListEntry oldItem, BlackListEntry newItem) {
                return oldItem != null && newItem != null && oldItem.phoneNumber.equals(newItem.phoneNumber);
            }

            @Override
            public boolean areContentsTheSame(BlackListEntry oldItem, BlackListEntry newItem) {
                return oldItem != null && oldItem.equals(newItem);
            }
        });

        this.listItemCollectionViewModel = viewModel;
        this.context = ctx;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {

        final BlackListEntry currentItem = getItem(position);

        holder.title.setText(currentItem.phoneNumber);
        holder.descriptor.setText(currentItem.contactName);
        holder.entryNumber.setText(Integer.toString(position + 1));

        holder.sendMessage.setOnCheckedChangeListener(null);
        holder.sendMessage.setChecked(currentItem.isSendingResponse);
        holder.sendMessage.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listItemCollectionViewModel.updateSendingText(currentItem.phoneNumber, b);
            }
        });

        holder.editMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LayoutInflater li = LayoutInflater.from(v.getContext());
                View customMessageView = li.inflate(R.layout.custom_message_entry_prompt, null);
                final EditText userInput = customMessageView.findViewById(R.id.addMessageEditText);
                final TextView displayMessage = customMessageView.findViewById(R.id.addMessageTextView);

                String message = currentItem.replyMessage;

                displayMessage.setText(message);


                //Initialize AlertDialogBuilder and pass custom_number_entry_prompt view reference to it
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setView(customMessageView);

                //Set alert buttons:
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String inputMessage = userInput.getText().toString();

                        String message2 = currentItem.replyMessage;

                        if (inputMessage.equals("")) {

                            dialog.dismiss();
                            Toast.makeText(context, "No message entered!", Toast.LENGTH_SHORT).show();
                        } else if (inputMessage.length() > 160) {
                            dialog.dismiss();
                            Toast.makeText(context, "Message May Not Exceed 160 Characters!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!inputMessage.equals(message2)) {
                                //Talk to repo
                                dialog.dismiss();
                                listItemCollectionViewModel.updateItemMessage(currentItem.phoneNumber, inputMessage);
                                Toast.makeText(context, "Message updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(context, "Desired message already set!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Message cleared!", Toast.LENGTH_SHORT).show();
                        listItemCollectionViewModel.updateItemMessage(currentItem.phoneNumber, "No custom message set!");
                    }
                });

                //Create AlertDialog, set keyboard to show on launch, and show AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
            }
        });
    }

    class MessageListViewHolder extends RecyclerView.ViewHolder {
        TextView entryNumber;
        TextView title;
        TextView descriptor;
        Switch sendMessage;
        ImageButton editMessage;

        MessageListViewHolder(View itemView) {
            super(itemView);
            entryNumber = itemView.findViewById(R.id.item_message_number);
            descriptor = itemView.findViewById(R.id.messageEntryTitle);
            title = itemView.findViewById(R.id.messageEntryDescriptor);
            editMessage = itemView.findViewById(R.id.message_button);
            sendMessage = itemView.findViewById(R.id.send_message_switch);
        }
    }
}
