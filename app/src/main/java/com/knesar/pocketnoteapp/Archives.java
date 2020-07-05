package com.knesar.navigationdemoapp;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "archives_table")
public class Archives {

    @PrimaryKey(autoGenerate = true)
    private int id1;

    private int color;
    private String items;
    private String description;

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public boolean equals(@Nullable Object obj) {
//        if (obj instanceof ManageNotes) {
//            ManageNotes other = (ManageNotes) obj;
//            return Objects.equals(this.items, other.items);
//        }
//        return super.equals(obj);
//
//    }

    public Archives(String items, int color, String description) {
        this.items = items;
        this.color = color;
        this.description = description;
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

    public void setItems(String items) {
        this.items = items;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }
}