package edu.gatech.phileckstrom.faradaycallshield.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
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


//Pattern Matching Documentation:

//https://developer.android.com/reference/java/util/regex/Pattern//
//https://stackoverflow.com/questions/28512464/how-to-match-regex-in-java
//http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
//https://www.w3schools.com/sql/sql_like.asp


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

        //TODO Check the reported cursor numbers of "Unknown" and "Private caller" incoming calls
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
        //TODO to correctly handle above
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
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);
            Class<?> telephonyServiceClass = Class.forName(telephonyService.getClass().getName());
            Method endCallMethod = telephonyServiceClass.getDeclaredMethod("endCall");
            endCallMethod.invoke(telephonyService);
        } catch (Exception e) {
            System.out.println("Exception when blocking call");
            e.printStackTrace();
        }

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
