package edu.gatech.phileckstrom.faradaycallshield.repository.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Objects;

@Entity
public class BlackListEntry {
    @ColumnInfo(name = "phone_number")
    @PrimaryKey
    @NonNull
    public String phoneNumber;
    @ColumnInfo(name = "contact_contact")
    public String contactName;
    @ColumnInfo(name = "reply_message")
    public String replyMessage;
    @ColumnInfo(name = "blocking_call")
    public boolean isBlockingCall;
    @ColumnInfo(name = "blocking_sms")
    public boolean isBlockingSMS;
    @ColumnInfo(name = "sending_response")
    public boolean isSendingResponse;

    public BlackListEntry(String phoneNumber, String contactName, String replyMessage) {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.replyMessage = replyMessage;
        this.isBlockingCall = true;
        this.isBlockingSMS = true;
        this.isSendingResponse = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListEntry entry = (BlackListEntry) o;
        return isBlockingCall == entry.isBlockingCall &&
                isBlockingSMS == entry.isBlockingSMS &&
                isSendingResponse == entry.isSendingResponse &&
                Objects.equals(phoneNumber, entry.phoneNumber) &&
                Objects.equals(contactName, entry.contactName) &&
                Objects.equals(replyMessage, entry.replyMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(phoneNumber, contactName, replyMessage, isBlockingCall, isBlockingSMS, isSendingResponse);
    }
}
