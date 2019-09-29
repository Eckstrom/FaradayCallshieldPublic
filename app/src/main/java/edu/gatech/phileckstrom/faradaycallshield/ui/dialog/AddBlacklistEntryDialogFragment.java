package edu.gatech.phileckstrom.faradaycallshield.ui.dialog;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.MainActivity;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.NewListItemViewModel;

import static android.app.Activity.RESULT_OK;

public class AddBlacklistEntryDialogFragment extends BottomSheetDialogFragment {
    Button recent_button, custom_button, contacts_button;
    String inputString;
    static final int PICK_CONTACT = 1;

    static final int PICK_CONTACT_REQUEST = 1;  // The request code


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private NewListItemViewModel newListItemViewModel;

    public AddBlacklistEntryDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        ((FaradayCallshieldApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.number_source_selection_new, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set up and subscribe (observe) to the ViewModel
        newListItemViewModel = ViewModelProviders.of(this, viewModelFactory).get(NewListItemViewModel.class);
    }

    private void startMainActivity() {

        Intent i = new Intent(getActivity(), MainActivity.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        //((Activity) getActivity()).overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recent_button = (Button) view.findViewById(R.id.recent_button);
        custom_button = (Button) view.findViewById(R.id.custom_button);
        contacts_button = (Button) view.findViewById(R.id.contacts_button);

        recent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri allCalls = Uri.parse("content://call_log/calls");

                Cursor cursor = getActivity().getContentResolver().query(allCalls, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

                final ArrayList<String> displayNames = new ArrayList<>();
                final HashMap<String, String> callList = new HashMap<>();

                int i = 0;

                cursor.moveToFirst();

                while (!cursor.isLast() || i < 10) {
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))) == CallLog.Calls.MISSED_TYPE
                            || Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))) == CallLog.Calls.BLOCKED_TYPE
                            || Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))) == CallLog.Calls.REJECTED_TYPE
                            || Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))) == CallLog.Calls.INCOMING_TYPE) {
                        String currentNum = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                        String currentName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

                        if (currentName == null)
                            currentName = currentNum;

                        if (currentName.length() > 25)
                            currentName = currentName.substring(0, 22) + "...";

                        callList.put(currentName, currentNum);

                        displayNames.add(currentName);

                    }
                    cursor.moveToNext();
                    i++;
                }
                cursor.close();

                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.lists, null);
                alertDialog.setView(convertView);
                final ListView lv = convertView.findViewById(R.id.recentCallsListView);
                final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, displayNames);
                lv.setAdapter(adapter2);
                alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        int pos = lv.getCheckedItemPosition();

                        if(pos == -1)
                        {
                            Toast.makeText(getContext(), "No number selected!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String name = lv.getAdapter().getItem(pos).toString();
                            String number = callList.get(name);
                            if(number.length()>10)
                            {
                                String modified = truncateNumber(number);
                                BlackListEntry modifiedEntry = new BlackListEntry(modified, name, "No custom message set!");
                                newListItemViewModel.addNewItemToDatabase(modifiedEntry);
                            }
                            else {
                                String modifiedRecentNumber = truncateNumber(number);
                                BlackListEntry entry = new BlackListEntry(modifiedRecentNumber, name, "No custom message set!");
                                newListItemViewModel.addNewItemToDatabase(entry);
                            }

                        }
                        AddBlacklistEntryDialogFragment.this.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AddBlacklistEntryDialogFragment.this.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        custom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.custom_number_entry_prompt, null);
                final EditText userInput = promptsView.findViewById(R.id.addNumberEditText);

                //Initialize AlertDialogBuilder and pass custom_number_entry_prompt view reference to it
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);

                //Set alert buttons:
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        inputString = userInput.getText().toString();
                        if(userInput.getText().toString().contains("(") || userInput.getText().toString().contains("/") ||
                           userInput.getText().toString().contains(")") || userInput.getText().toString().contains("N") ||
                           userInput.getText().toString().contains(",") || userInput.getText().toString().contains(".") ||
                           userInput.getText().toString().contains("*") || userInput.getText().toString().contains(";") ||
                           userInput.getText().toString().contains("-") || userInput.getText().toString().contains("+") ||
                           userInput.getText().toString().contains(" ")){

                            userInput.setError("Please enter '#' to wild-card a digit.");
                        }
                        else{

                            if(newListItemViewModel.containsNumber(inputString).size()>0)
                                Toast.makeText(getContext(), "Number already blacklisted!", Toast.LENGTH_SHORT).show();
                            else if (inputString.length() != 10)
                                Toast.makeText(getContext(), "Phone number must be 10 digits!", Toast.LENGTH_SHORT).show();
                            else {
                                String contactName = inputString;

                                Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(inputString));
                                Cursor phonesCursor = getContext().getContentResolver().query(phoneUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

                                if (phonesCursor != null && phonesCursor.moveToFirst()) {
                                    contactName = phonesCursor.getString(0); // this is the contact name
                                }
                                phonesCursor.close();

                                String modifiedCustomNumber = truncateNumber(inputString);

                                BlackListEntry entry = new BlackListEntry(modifiedCustomNumber, contactName, "No custom message set!");
                                newListItemViewModel.addNewItemToDatabase(entry);
                                AddBlacklistEntryDialogFragment.this.dismiss();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AddBlacklistEntryDialogFragment.this.dismiss();
                    }
                });

                //Create AlertDialog, set keyboard to show on launch, and show AlertDialog
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
            }
        });
    }


    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                String contactName = cursor.getString(column2);
                String number = cursor.getString(column);

                String newnumber = number.replaceAll("[(/)N,.*;+ -]","");

                String nextNewNumber = newnumber.replaceAll("[(/)N,.*;+ -]","");

                String finalNumber;

                if(nextNewNumber.length() > 10)
                {
                    finalNumber = truncateNumber(nextNewNumber);
                }
                else
                {
                    finalNumber = nextNewNumber;
                }

                if(finalNumber.length() < 10)
                {
                    Toast.makeText(getContext(), "Error. Contact number must be 10 digits!", Toast.LENGTH_SHORT).show();
                    AddBlacklistEntryDialogFragment.this.dismiss();

                }
                else {
                    System.out.println("The selected number: " + finalNumber + " and their contact is: " + contactName);

                    BlackListEntry entry = new BlackListEntry(finalNumber, contactName, "No custom message set!");
                    newListItemViewModel.addNewItemToDatabase(entry);
                    AddBlacklistEntryDialogFragment.this.dismiss();
                }
            }
        }
    }

    public String truncateNumber(String number)
    {
        return number.substring(number.length()-10);
    }
}