package com.example.christina.carna_ui.database;

import java.io.Serializable;

/**
 * Created by oguzbinbir on 14.06.16.
 */

public class AngelMemoWerte implements Serializable {

    private int wertId;
    private int userId;
    private int sensorId;

    private String wert;
    private String datum;


    public AngelMemoWerte(int wertId, int userId, int sensorId, String wert, String datum) {
        this.wertId = wertId;
        this.userId = userId;
        this.sensorId = sensorId;
        this.wert = wert;
        this.datum = datum;
    }

    public int getWertId() { return wertId; }
    public void setWertId(int wertId){ this.wertId = wertId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getSensorId() { return sensorId; }
    public void setSensorId(int sensorId){ this.sensorId = sensorId; }

    public String getWert(){ return wert; }
    public void setWert(String wert){this.wert = wert; }

    public String getDatum() { return datum; }

    public void setDatum(String datum) { this.datum = datum; }

    @Override
    public String toString() {
        String output = wertId + " : " + userId + " : " + sensorId + " : " + wert + " : " + datum;
        return output;
    }
}