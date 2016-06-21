package com.example.christina.carna_ui.database;

import java.io.Serializable;

/**
 * Created by oguzbinbir on 14.06.16.
 */

public class AngelMemoSensor implements Serializable {

    private int sensorId;
    private String sensorName;


    public AngelMemoSensor(int sensorId, String sensorName) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
    }


    public String getSensorName() {
        return sensorName;
    }
    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public int getSensorId() {
        return sensorId;
    }
    public void setSensorId(int sensorId) {
        this.sensorId= sensorId;
    }


    @Override
    public String toString() {
        String output = sensorId + " : " + sensorName;
        return output;
    }
}