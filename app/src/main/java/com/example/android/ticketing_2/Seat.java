package com.example.android.ticketing_2;

public class Seat {
    private String gateNo;
    private int row;
    private int col;
    private boolean free;
    private int color;

    public Seat(String gateNo, int row, int col, boolean free, int color) {
        this.gateNo = gateNo;
        this.row = row;
        this.col = col;
        this.free = free;
        this.color = color;
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

    public int getColor() {
        return color;
    }
}
