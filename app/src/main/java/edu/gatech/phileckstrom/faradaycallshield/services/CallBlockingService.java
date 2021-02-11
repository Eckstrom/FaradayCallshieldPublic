package edu.gatech.phileckstrom.faradaycallshield.services;

import android.content.Intent;
import android.os.IBinder;
import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

//Prototype for CallScreeningService induction after API 24.
//This service must be used to block calls on any Android device that is running >API 24.
//********INCOMPLETE********//
public class CallBlockingService extends CallScreeningService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("Got to onBind");
        return null;
    }

    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        System.out.println("Got to onCallScreen");
        if (details.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
            boolean isAllowed = true; // Check if blacklist

            List<String> uri = details.getHandle().getPathSegments();
            String number = uri.get(1);

            if (isAllowed) {
                // Accept the call
                respondToCall(details, new CallResponse.Builder().build());
            }
        }
    }

}