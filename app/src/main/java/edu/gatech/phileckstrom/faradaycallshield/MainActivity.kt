package edu.gatech.phileckstrom.faradaycallshield

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import edu.gatech.phileckstrom.faradaycallshield.misc.BaseActivity
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem
import edu.gatech.phileckstrom.faradaycallshield.requestors.PermissionRequestor
import edu.gatech.phileckstrom.faradaycallshield.requestors.RoleRequestor
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionDenied
import edu.gatech.phileckstrom.faradaycallshield.services.PermissionsEnabled
import edu.gatech.phileckstrom.faradaycallshield.ui.adapter.MainPagerAdapter
import edu.gatech.phileckstrom.faradaycallshield.viewmodel.ListItemCollectionViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val permissionRequester = PermissionRequestor()
    private val capabilitiesRequestor = RoleRequestor()

    @JvmField
    @Inject
    var viewModelFactory: ViewModelProvider.Factory? = null
    private lateinit var listItemCollectionViewModel: ListItemCollectionViewModel
    var defaultSMSButton: ImageButton? = null
    var defaultSMSSwitch: Switch? = null
    var forJason = false
    private var checkCapabilitiesOnResume = false

    public override fun onCreate(savedInstanceState: Bundle?) {

        //Changing dependency injection location from HERE

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listenUiEvents()

        permissionRequester.activity = WeakReference(this)
        capabilitiesRequestor.activityReference = WeakReference(this)
        permissionRequester.getPermissions()

        //TO HERE somehow caused a difference in whether the app wants to reject calls or not
        //WHY IS THIS?
        (application as FaradayCallshieldApplication)
                .applicationComponent
                .inject(this)

        //Set up and subscribe (observe) to the ViewModel
        listItemCollectionViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ListItemCollectionViewModel::class.java)
        if (listItemCollectionViewModel.containsDefaultSMS("1").size == 0) {
            val defaultSMSResponse = DefaultSMSItem("1", "No custom message set!", 0)
            listItemCollectionViewModel.addNewDefaultSMSToDatabase(defaultSMSResponse)
        }
        setupDefaultButton()
        //setupDefaultSwitch();
        setupTabLayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        capabilitiesRequestor.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    override fun onResume() {
        super.onResume()
        if (checkCapabilitiesOnResume) {
            capabilitiesRequestor.invokeCapabilitiesRequest()
            checkCapabilitiesOnResume = false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupDefaultSwitch() {
        defaultSMSSwitch = findViewById(R.id.defaultSMSSwitch)
        val currentItem = listItemCollectionViewModel.containsDefaultSMS("1")[0]
        defaultSMSSwitch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val checkInt: Int
            checkInt = if (isChecked) 1 else 0
            listItemCollectionViewModel.updateSendingDefaultSMS(currentItem.message, checkInt)
        })
        if (!forJason) {
            defaultSMSSwitch?.visibility = View.GONE
        }
    }

    private fun listenUiEvents() {
        uiEvent.observe(this, {
            when (it) { //
                is PermissionDenied -> {
                    checkCapabilitiesOnResume = true
                    AppSettingsDialog.Builder(this).build().show()
                }
                is PermissionsEnabled -> {
                    capabilitiesRequestor.invokeCapabilitiesRequest()
                }
                else -> {
                    //Empty
                }
            }
        })
    }

    private fun setupDefaultButton() {

        defaultSMSButton = findViewById(R.id.defaultSMSButton)
        defaultSMSButton?.setOnClickListener { view ->
            val currentItem = listItemCollectionViewModel.containsDefaultSMS("1")[0]
            val li = LayoutInflater.from(view.context)
            val customMessageView = li.inflate(R.layout.custom_message_entry_prompt, null)
            val userInput = customMessageView.findViewById<EditText>(R.id.addMessageEditText)
            val displayMessage = customMessageView.findViewById<TextView>(R.id.addMessageTextView)
            val message = currentItem.message
            displayMessage.text = message

            //Initialize AlertDialogBuilder and pass custom_number_entry_prompt view reference to it
            val alertDialogBuilder = AlertDialog.Builder(view.context)
            alertDialogBuilder.setView(customMessageView)

            //Set alert buttons:
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK") { dialog, id ->
                val inputMessage = userInput.text.toString()
                val message2 = currentItem.message
                if (inputMessage == "") {
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, "No message entered!", Toast.LENGTH_SHORT).show()
                } else if (inputMessage.length > 160) {
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, "Message May Not Exceed 160 Characters!", Toast.LENGTH_SHORT).show()
                } else {
                    if (inputMessage != message2) {
                        //Talk to repo
                        dialog.dismiss()
                        listItemCollectionViewModel.updateDefaultSMSMessage(currentItem.locator, inputMessage)
                        Toast.makeText(this@MainActivity, "Message updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.dismiss()
                        Toast.makeText(this@MainActivity, "Desired message already set!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.setNegativeButton("Cancel") { dialog, id -> dialog.dismiss() }.setNeutralButton("Clear") { dialog, id ->
                Toast.makeText(applicationContext, "Message cleared!", Toast.LENGTH_SHORT).show()
                listItemCollectionViewModel.updateDefaultSMSMessage(currentItem.locator, "No custom message set!")
            }

            //Create AlertDialog, set keyboard to show on launch, and show AlertDialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            alertDialog.show()
        }
        if (!forJason) {
            defaultSMSButton?.visibility = View.GONE
        }
    }

    private fun setupTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tablayout)
        tabLayout.addTab(tabLayout.newTab().setText("Blacklist"))
        tabLayout.addTab(tabLayout.newTab().setText("AUTO-SMS Response"))
        tabLayout.addTab(tabLayout.newTab().setText("Activity Log"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val viewPager = findViewById<ViewPager>(R.id.pager)
        val adapter = MainPagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        //viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}