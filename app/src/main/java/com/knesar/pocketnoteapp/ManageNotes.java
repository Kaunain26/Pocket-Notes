package com.knesar.navigationdemoapp;


import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "manageNotes_table")
public class ManageNotes {

    @PrimaryKey(autoGenerate = true)
    private int id1;

    private int color;
    private String items;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ManageNotes) {
            ManageNotes other = (ManageNotes) obj;
            return Objects.equals(this.items, other.items);
        }
        return super.equals(obj);

    }

    public ManageNotes(String items, int color) {
        this.items = items;
        this.color = color;
    }


    public int getId1() {
        return id1;
    }

    public int getColor() {
        return color;
    }

    public void setId1(int id) {
        this.id1 = id;
    }

    public String getItems() {
        return items;
    }

}