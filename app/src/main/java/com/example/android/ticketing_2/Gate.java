package com.example.android.ticketing_2;

public class Gate {
    private String gateNo;
    private int freeSeats;
    private float price;
    private String color;

    public Gate(String gateNo, int numberOfSeats, float price, String color) {
        this.gateNo = gateNo;
        this.freeSeats = numberOfSeats;
        this.price = price;
        this.color = color;
    }

    public Gate() {
    }

    public String getGateNo() {
        return gateNo;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public float getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }
}


