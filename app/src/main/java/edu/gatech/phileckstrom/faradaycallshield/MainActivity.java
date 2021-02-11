package edu.gatech.phileckstrom.faradaycallshield;

import android.Manifest;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.misc.BaseActivity;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.misc.MainConstants;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;
import edu.gatech.phileckstrom.faradaycallshield.ui.adapter.MainPagerAdapter;
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel;

import static android.view.View.GONE;


public class MainActivity extends BaseActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public ListItemCollectionViewModel listItemCollectionViewModel;

    ImageButton defaultSMSButton;
    Switch defaultSMSSwitch;

    boolean forJason = false;

    private static final int REQUEST_ID = 1;

    public void requestRole() {
        RoleManager roleManager = (RoleManager) getSystemService(Context.ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, REQUEST_ID);
    }

    //This is for CallScreeningService **PROTOTYPED**
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ID) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                System.out.println("Became the callscreening service.");
                // Your app is now the call screening app
            } else {
                System.out.println("Did NOT become the callscreening service.");
                // Your app is not the call screening app
                // Create UI Prompt for service acceptance here
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//
//        if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_CONTACTS)) {
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_PHONE_STATE},
//                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//            }
//        }

        ((FaradayCallshieldApplication) getApplication())
                .getApplicationComponent()
                .inject(this);

        //Set up and subscribe (observe) to the ViewModel
        listItemCollectionViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ListItemCollectionViewModel.class);

        if (listItemCollectionViewModel.containsDefaultSMS("1").size() == 0) {
            DefaultSMSItem defaultSMSResponse = new DefaultSMSItem("1", "No custom message set!", 0);

            listItemCollectionViewModel.addNewDefaultSMSToDatabase(defaultSMSResponse);
        }

        //Set Content
        setContentView(R.layout.activity_main);

        setupDefaultButton();
        //setupDefaultSwitch();

        setupTabLayout();
        requestPermissions();
        requestRole();

        boolean isdef = isMyAppLauncherDefault();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void setupDefaultSwitch() {

        defaultSMSSwitch = findViewById(R.id.defaultSMSSwitch);

        final DefaultSMSItem currentItem = listItemCollectionViewModel.containsDefaultSMS("1").get(0);

        defaultSMSSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int checkInt;

                if (isChecked)
                    checkInt = 1;
                else
                    checkInt = 0;

                listItemCollectionViewModel.updateSendingDefaultSMS(currentItem.message, checkInt);

            }
        });

        if (!forJason) {
            defaultSMSSwitch.setVisibility(GONE);
        }
    }

    private void setupDefaultButton() {

        defaultSMSButton = findViewById(R.id.defaultSMSButton);

        defaultSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final DefaultSMSItem currentItem = listItemCollectionViewModel.containsDefaultSMS("1").get(0);

                final LayoutInflater li = LayoutInflater.from(view.getContext());
                View customMessageView = li.inflate(R.layout.custom_message_entry_prompt, null);
                final EditText userInput = customMessageView.findViewById(R.id.addMessageEditText);
                final TextView displayMessage = customMessageView.findViewById(R.id.addMessageTextView);

                String message = currentItem.message;

                displayMessage.setText(message);


                //Initialize AlertDialogBuilder and pass custom_number_entry_prompt view reference to it
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(customMessageView);

                //Set alert buttons:
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String inputMessage = userInput.getText().toString();

                        String message2 = currentItem.message;

                        if (inputMessage.equals("")) {

                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "No message entered!", Toast.LENGTH_SHORT).show();
                        } else if (inputMessage.length() > 160) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Message May Not Exceed 160 Characters!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!inputMessage.equals(message2)) {
                                //Talk to repo
                                dialog.dismiss();

                                listItemCollectionViewModel.updateDefaultSMSMessage(currentItem.locator, inputMessage);
                                Toast.makeText(MainActivity.this, "Message updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "Desired message already set!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Message cleared!", Toast.LENGTH_SHORT).show();
                        listItemCollectionViewModel.updateDefaultSMSMessage(currentItem.locator, "No custom message set!");
                    }
                });

                //Create AlertDialog, set keyboard to show on launch, and show AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
            }
        });

        if (!forJason) {
            defaultSMSButton.setVisibility(GONE);
        }
    }

    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Blacklist"));
        tabLayout.addTab(tabLayout.newTab().setText("AUTO-SMS Response"));
        tabLayout.addTab(tabLayout.newTab().setText("Activity Log"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = findViewById(R.id.pager);
        final MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, MainConstants.READ_CALL_LOG);
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MainConstants.SEND_SMS);
        // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MainConstants.READ_CONTACTS);
    }
}


