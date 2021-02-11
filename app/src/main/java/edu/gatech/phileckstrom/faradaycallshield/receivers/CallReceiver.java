package edu.gatech.phileckstrom.faradaycallshield.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.inject.Inject;

import edu.gatech.phileckstrom.faradaycallshield.R;
import edu.gatech.phileckstrom.faradaycallshield.misc.MainConstants;
import edu.gatech.phileckstrom.faradaycallshield.misc.FaradayCallshieldApplication;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry;
import edu.gatech.phileckstrom.faradaycallshield.repository.BlacklistEntryDatabase;
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem;

//Main Call Receiver class. This class is now deprecated since CallScreeningService
public class CallReceiver extends PhonecallReceiver {

    @Inject
    BlacklistEntryDatabase db;

    @Override
    protected void onIncomingCallReceived(final Context ctx, final String number, Date start) {

        ((FaradayCallshieldApplication) ctx.getApplicationContext()).getApplicationComponent().inject(this);

        Executors.newSingleThreadExecutor()
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        String date = getDate();
                        String time = getTime();

                        String modifiedIncomingNumber = truncateNumber(number);
                        List<BlackListEntry> matches = db.blackListEntryDao().getNumberMatches(modifiedIncomingNumber);
                        final DefaultSMSItem defaultSMSResponse = db.defaultSMSItemDao().getMessagePresence("1").get(0);

                        String contact;

                        if (matches.size() > 0) {
                            BlackListEntry entry = matches.get(0);
                            contact = entry.contactName;
                            blockCall(ctx);
                            if (entry.isSendingResponse && !entry.replyMessage.equals("No custom message set!")) {
                                sendSMS(modifiedIncomingNumber, entry.replyMessage);
                                generateNotification(MainConstants.BLOCKED_CALL, contact, false, true, ctx);
                            }
                            else if(entry.isSendingResponse && entry.replyMessage.equals("No custom message set!") && !defaultSMSResponse.message.equals("No custom message set!")){
                                generateNotification(MainConstants.BLOCKED_CALL, contact, true, true, ctx);
                                sendSMS(modifiedIncomingNumber, defaultSMSResponse.message);
                            }
                            else {
                                generateNotification(MainConstants.BLOCKED_CALL, contact, false, false, ctx);
                            }
                            updateCallLog(time, date, contact);

                            return;
                        }

                        List<BlackListEntry> entriesPound = db.blackListEntryDao().getNumberMatchesWithPound();

                        if (entriesPound.size() > 0) {
                            for (int i = 0; i < entriesPound.size(); i++) {

                                String currMessage = entriesPound.get(i).replyMessage;
                                boolean isSending = entriesPound.get(i).isSendingResponse;
                                Pattern p = Pattern.compile((entriesPound.get(i).phoneNumber.replace("#", "\\d")));

                                if (p.matcher(modifiedIncomingNumber).matches()) {
                                    blockCall(ctx);
                                    if (isSending && currMessage.equals("No custom message set!")) {
                                        sendSMS(modifiedIncomingNumber, currMessage);
                                        generateNotification(MainConstants.BLOCKED_CALL, number, false, true, ctx);
                                    }
                                    else if(isSending && currMessage.equals("No custom message set!") && !defaultSMSResponse.message.equals("No custom message set!")){
                                        sendSMS(modifiedIncomingNumber, defaultSMSResponse.message);
                                        generateNotification(MainConstants.BLOCKED_CALL, number, true, true, ctx);
                                    }
                                    else {
                                        generateNotification(MainConstants.BLOCKED_CALL, number, false, false, ctx);
                                    }
                                    updateCallLog(time, date, number);
                                    return;
                                }
                            }
                        }
                    }
                });

        //TODO Handle UNKNOWN numbers here
//        if(number == "-2")
//            {
//                System.out.println("Null number was rejected.");
//
//                TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
//                Class<?> c = Class.forName(tm.getClass().getName());
//                Method m = c.getDeclaredMethod("getITelephony");
//                m.setAccessible(true);
//                Object telephonyService = m.invoke(tm);
//                Class<?> telephonyServiceClass = Class.forName(telephonyService.getClass().getName());
//                Method endCallMethod = telephonyServiceClass.getDeclaredMethod("endCall");
//                endCallMethod.invoke(telephonyService);
//            }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        
    }


    private String getTime() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        String dateToStr = format.format(today);
        System.out.println(dateToStr);

        return dateToStr;
    }

    private String getDate() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String dateToStr = format.format(today);
        System.out.println(dateToStr);

        return dateToStr;
    }


    //Overriden methods to handle cases where calls are answered, ended, etc:
    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        //
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        //
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        //
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        //
    }

    private void blockCall(Context ctx) {
        Context ctx2 = ctx.getApplicationContext();
        TelephonyManager tm = (TelephonyManager) ctx2.getSystemService(Context.TELEPHONY_SERVICE);

        CallScreeningService sv = new CallScreeningService() {
            @Override
            public void onScreenCall(Call.Details callDetails) {
                CallScreeningService.CallResponse response =
                        new CallScreeningService.CallResponse.Builder().setDisallowCall(true).setRejectCall(true)
                                .setSkipCallLog(false).setSkipNotification(true).build();

                this.respondToCall(callDetails, response);
            }
        };

//        try {
//            Class<?> classTelephony = Class.forName(tm.getClass().getName());
//            Method method = classTelephony.getDeclaredMethod("getITelephony");
//            method.setAccessible(true);
//            Object telephonyInterface = method.invoke(tm);
//            Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
//            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
//            methodEndCall.invoke(telephonyInterface);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }//11111111111111111111
    }

    public void updateCallLog(String time, String date, String number) {

        ActivityLogItem currentCaller = new ActivityLogItem(date, time, number);
        db.dateActivityDao().insert(currentCaller);
    }


    private void generateNotification(int callOrSMSBlocked, String contactOrNumber, boolean isDefault, boolean autoSMSSent, Context ctx) {
        String blockType;
        String cappedBlockType;
        String text;

        if (callOrSMSBlocked == MainConstants.BLOCKED_CALL) {
            blockType = "call";
            cappedBlockType = "Call";
        } else {
            blockType = "SMS";
            cappedBlockType = "SMS";
        }


        if (autoSMSSent){
            text = "Blocked " + blockType + " from " + contactOrNumber + ", auto-SMS sent.";
        }
        else if(autoSMSSent && isDefault){
            text = "Blocked " + blockType + " from " + contactOrNumber + ", default auto-SMS sent.";
        }
        else{
            text = "Blocked " + blockType + " from " + contactOrNumber;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx, "1")
                        .setSmallIcon(R.drawable.faradayicon2)
                        .setContentTitle(cappedBlockType + " blocked")
                        .setContentText(text);

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    public String truncateNumber(String number) {
        return number.substring(number.length() - 10);
    }

}
