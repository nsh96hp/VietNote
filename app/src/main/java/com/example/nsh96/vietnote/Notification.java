package com.example.nsh96.vietnote;

public class Notification {
    private int time;
    private int dv;

    public Notification() {
    }

    public Notification(int time, int dv) {
        this.time = time;
        this.dv = dv;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDv() {
        return dv;
    }

    public void setDv(int dv) {
        this.dv = dv;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "time=" + time +
                ", dv=" + dv +
                '}';
    }
}
