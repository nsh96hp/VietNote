package com.example.nsh96.vietnote;

public class SortNote {
    private int updown; //Up=0, Down =1
    private int styleSort; //time created==0 (ID), title==1 , time alarm ==2

    public SortNote(int updown, int styleSort) {
        this.updown = updown;
        this.styleSort = styleSort;
    }

    public SortNote() {
    }

    public int getUpdown() {
        return updown;
    }

    public void setUpdown(int updown) {
        this.updown = updown;
    }

    public int getStyleSort() {
        return styleSort;
    }

    public void setStyleSort(int styleSort) {
        this.styleSort = styleSort;
    }
}
