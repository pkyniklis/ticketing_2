package com.example.android.ticketing_2;

public class Seat {
    private String gateNo;
    private int row;
    private int col;
    private boolean free;
    private String color;

    public Seat(String gateNo, int row, int col, boolean free, String color) {
        this.gateNo = gateNo;
        this.row = row;
        this.col = col;
        this.free = free;
        this.color = color;
    }

    public Seat() {
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

    public String getColor() {
        return color;
    }
}
