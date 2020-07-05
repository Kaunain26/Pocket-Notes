package com.knesar.navigationdemoapp;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "notes_table")
public class NotesEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int color;
    private String title;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj instanceof NotesEntity) {
            NotesEntity other = (NotesEntity) obj;
            return Objects.equals(this.title, other.title);
        }
        return super.equals(obj);
    }

    public NotesEntity(String title, int color) {
        this.title = title;
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}

