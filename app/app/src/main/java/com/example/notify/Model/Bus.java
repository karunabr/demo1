package com.example.sjecnotify.Model;

public class Bus {
    private String time,busnumber,stops,driverName,driverNumber;

    public Bus()
    {

    }

    public Bus(String time, String busnumber, String stops, String driverName, String driverNumber) {
        this.time = time;
        this.busnumber = busnumber;
        this.stops = stops;
        this.driverName = driverName;
        this.driverNumber = driverNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBusnumber() {
        return busnumber;
    }

    public void setBusnumber(String busnumber) {
        this.busnumber = busnumber;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }
}
