package com.example.android.ticketing_2;

public class Seat {
    private String gateNo;
    private int row;
    private int col;
    private boolean free;
    private boolean booked;
    private String color;
    private String userId;

    public Seat(String gateNo, int row, int col, boolean free, boolean booked, String color, String userId) {
        this.gateNo = gateNo;
        this.row = row;
        this.col = col;
        this.free = free;
        this.booked = booked;
        this.color = color;
        this.userId = userId;
    }

    public Seat() {
        this.gateNo = "";
        this.row = 0;
        this.col = 0;
        this.free = true;
        this.booked = false;
        this.color = "";
        this.userId = "";
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getColor() {
        return color;
    }
}
