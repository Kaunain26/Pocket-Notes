package com.knesar.navigationdemoapp;

import java.util.Random;

public class CustomCategoryModelClass {
    private int id;
    private String task;
    private int color;
    Random rand = new Random();

    public CustomCategoryModelClass(String task, int color) {
        this.task = task;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return rand.nextInt();
    }

    public String getTask() {
        return task;
    }
}
