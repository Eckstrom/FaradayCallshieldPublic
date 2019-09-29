package edu.gatech.phileckstrom.faradaycallshield.repository.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity (tableName = "ActivityLogItem")
public class ActivityLogItem {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    public long id = 0;
    @ColumnInfo(name = "date")
    public String date;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "caller")
    public String caller;

    public ActivityLogItem(String date, String time, String caller) {
        this.date = date;
        this.caller = caller;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityLogItem that = (ActivityLogItem) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(caller, that.caller);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, time, caller);
    }
}
