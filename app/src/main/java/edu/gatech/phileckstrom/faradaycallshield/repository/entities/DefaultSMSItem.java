package edu.gatech.phileckstrom.faradaycallshield.repository.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity (tableName = "DefaultSMSItem")
public class DefaultSMSItem {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    public long id = 0;
    @ColumnInfo(name = "locator")
    public String locator;
    @ColumnInfo(name = "message")
    public String message;
    @ColumnInfo(name = "sending")
    public int sending;

    public DefaultSMSItem(String locator, String message, int sending) {
        this.locator = locator;
        this.message = message;
        this.sending = sending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultSMSItem that = (DefaultSMSItem) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(locator, that.locator) &&
                Objects.equals(sending, that.sending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locator, message, sending);
    }
}
