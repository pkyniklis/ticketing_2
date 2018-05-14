package com.example.android.ticketing_2;

public class SeatStringFunctions {

    public static String getGate(String seatInfo) {
        // https://stackoverflow.com/questions/12595019/how-to-get-a-string-between-two-characters
        return seatInfo.substring(seatInfo.indexOf("g") + 1, seatInfo.indexOf("r"));
    }

    public static String getRow(String seatInfo) {
        return seatInfo.substring(seatInfo.indexOf("r") + 1, seatInfo.indexOf("s"));
    }

    public static String getSeat(String seatInfo) {
        return seatInfo.substring(seatInfo.indexOf("s") + 1, seatInfo.indexOf("p"));
    }

    public static String getRowAndSeat(String seatInfo) {
        return seatInfo.substring(seatInfo.indexOf("r") + 1, seatInfo.indexOf("p"));
    }

    public static int getPrice(String seatInfo) {
        return Integer.parseInt(seatInfo.substring(seatInfo.indexOf("p") + 1));
    }

}
