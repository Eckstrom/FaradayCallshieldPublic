package edu.gatech.phileckstrom.faradaycallshield.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

//*******THIS CLASS IS DEPRECATED WITH THE INDUCTION OF CALLSCREENINGSERVICE FROM GOOGLE*******//
public abstract class PhonecallReceiver extends BroadcastReceiver
{
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getStringExtra("PHONE_NUMBER");
            savedNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        }
        else{
            savedNumber = intent.getStringExtra("PHONE_NUMBER");
            savedNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    try {
                        onCallStateChanged2(context, state, incomingNumber);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }finally {
                        tm.listen(this, PhoneStateListener.LISTEN_NONE);
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    protected abstract void onIncomingCallReceived(Context ctx, String number, Date start) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
    protected abstract void onIncomingCallAnswered(Context ctx, String number, Date start);
    protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);
    protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);
    protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);
    protected abstract void onMissedCall(Context ctx, String number, Date start);

    public void onCallStateChanged2(Context context, int state, String number) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(lastState == state){

            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallReceived(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:

                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}
