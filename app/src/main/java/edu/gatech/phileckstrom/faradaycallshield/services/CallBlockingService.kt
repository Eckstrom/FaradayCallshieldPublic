package edu.gatech.phileckstrom.faradaycallshield.services

import android.app.NotificationManager
import android.content.Context
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import edu.gatech.phileckstrom.faradaycallshield.R
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistEntryDatabase
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class CallBlockingService : CallScreeningService() {

    @Inject
    lateinit var db: BlacklistEntryDatabase

    override fun onCreate() {
        (application as FaradayCallshieldApplication)
                .getApplicationComponent()
                .inject(this)
        super.onCreate()
    }

    override fun onScreenCall(callDetails: Call.Details) {
        Log.d("Screened", "Screened a call!")
        val phoneNumber = getPhoneNumber(callDetails)
        var response = CallResponse.Builder()
        response = handlePhoneCall(response, phoneNumber)

        respondToCall(callDetails, response.build())
    }

    private fun handlePhoneCall(response: CallResponse.Builder, phoneNumber: String): CallResponse.Builder {

        val date = getDate()
        val time = getTime()

        if(db.blackListEntryDao()?.getNumberMatches(phoneNumber) == null)
            Log.d("Nullify", "Yep, it's still null, Jim")

        val matches = db.blackListEntryDao()?.getNumberMatches(phoneNumber)
        val defaultSMSResponse = db.defaultSMSItemDao()?.getMessagePresence("1")?.get(0)

        val contact: String

        if (matches != null) {
            if (matches.size > 0) {
                val entry = matches[0]
                contact = entry.contactName
                with(response) {
                    setRejectCall(true)
                    setDisallowCall(true)
                    setSkipCallLog(false)
                    //
                    displayToast(String.format("Rejected call from %s", phoneNumber))
                }
                if (entry.isSendingResponse && entry.replyMessage != "No custom message set!") {
                    sendSMS(phoneNumber, entry.replyMessage)
                    generateNotification(BLOCKED_CALL, contact, false, true, this)
                } else if (defaultSMSResponse != null) {
                    if (entry.isSendingResponse && entry.replyMessage == "No custom message set!" && defaultSMSResponse.message != "No custom message set!") {
                        generateNotification(BLOCKED_CALL, contact, true, true, this)
                        sendSMS(phoneNumber, defaultSMSResponse.message)
                    } else {
                        generateNotification(BLOCKED_CALL, contact, false, false, this)
                    }
                }
                updateCallLog(time, date, contact)
            }
        }

        val entriesPound = db.blackListEntryDao()?.numberMatchesWithPound

        if (entriesPound != null) {
            if (entriesPound.size > 0) {
                for (i in entriesPound.indices) {
                    val currMessage = entriesPound[i].replyMessage
                    val isSending = entriesPound[i].isSendingResponse
                    val p = Pattern.compile(entriesPound[i].phoneNumber.replace("#", "\\d"))
                    if (p.matcher(phoneNumber).matches()) {
                        with(response) {
                            setRejectCall(true)
                            setDisallowCall(true)
                            setSkipCallLog(false)
                            //
                            displayToast(String.format("Rejected call from %s", phoneNumber))
                        }
                        if (isSending && currMessage == "No custom message set!") {
                            sendSMS(phoneNumber, currMessage)
                            generateNotification(BLOCKED_CALL, phoneNumber, false, true, this)
                        } else if (defaultSMSResponse != null) {
                            if (isSending && currMessage == "No custom message set!" && defaultSMSResponse.message != "No custom message set!") {
                                sendSMS(phoneNumber, defaultSMSResponse.message)
                                generateNotification(BLOCKED_CALL, phoneNumber, true, true, this)
                            } else {
                                generateNotification(BLOCKED_CALL, phoneNumber, false, false, this)
                            }
                        }
                        updateCallLog(time, date, phoneNumber)
                    }
                }
            } else{
                with(response) {
                    setRejectCall(true)
                    setDisallowCall(true)
                    setSkipCallLog(false)
                    //
                    displayToast(String.format("Rejected call from %s", phoneNumber))
                }
            }
        }
        return response
    }

    private fun handlePhoneCall2(response: CallResponse.Builder, number: String): CallResponse.Builder {

        response.setRejectCall(true).setDisallowCall(true).setSkipCallLog(false)
        displayToast(String.format("Rejected call from: ", number))
        return response
    }

    private fun getPhoneNumber(callDetails: Call.Details): String {
        return callDetails.handle.toString().removeTelPrefix().parseCountryCode()
    }

    private fun generateNotification(callOrSMSBlocked: Int, contactOrNumber: String, isDefault: Boolean, autoSMSSent: Boolean, ctx: Context) {
        val blockType: String
        val cappedBlockType: String
        val text: String
        if (callOrSMSBlocked == BLOCKED_CALL) {
            blockType = "call"
            cappedBlockType = "Call"
        } else {
            blockType = "SMS"
            cappedBlockType = "SMS"
        }
        text = if (autoSMSSent) {
            "Blocked $blockType from $contactOrNumber, auto-SMS sent."
        } else if (autoSMSSent && isDefault) {
            "Blocked $blockType from $contactOrNumber, default auto-SMS sent."
        } else {
            "Blocked $blockType from $contactOrNumber"
        }
        val mBuilder = NotificationCompat.Builder(ctx, "1")
                .setSmallIcon(R.drawable.faradayicon2)
                .setContentTitle("$cappedBlockType blocked")
                .setContentText(text)
        val notificationManager = ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, mBuilder.build())
    }

    fun updateCallLog(time: String?, date: String?, number: String?) {
        val currentCaller = ActivityLogItem(date, time, number)
        //db!!.dateActivityDao().insert(currentCaller)
    }

    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getTime(): String? {
        val today = Date()
        val format = SimpleDateFormat("hh:mm:ss a")
        val dateToStr = format.format(today)
        println(dateToStr)
        return dateToStr
    }

    private fun getDate(): String? {
        val today = Date()
        val format = SimpleDateFormat("MM-dd-yyyy")
        val dateToStr = format.format(today)
        println(dateToStr)
        return dateToStr
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}