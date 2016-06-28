package com.example.christina.carna_ui.database;

/**
 * Created by oguzbinbir on 07.06.16.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.christina.carna_ui.enumclass.SensorType;

import java.util.ArrayList;
import java.util.List;


public class AngelMemoDataSource {

    private static final String LOG_TAG = AngelMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private AngelMemoDbHelper dbHelper;


    public AngelMemoDataSource(Context context) {
        try {
            Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
            dbHelper = new AngelMemoDbHelper(context);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler DataSource: " + e.getMessage());
        }
    }

    public void open() {
        try {
            Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
            database = dbHelper.getWritableDatabase();
            Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
            database.setForeignKeyConstraintsEnabled(true);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler open: " + e.getMessage());
        }
    }

    public void close() {
        try {
            dbHelper.close();
            Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler close: " + e.getMessage());
        }
    }


    public void addUser(String name){

        try {
            ContentValues values = new ContentValues();
            values.put(AngelMemoDbHelper.COLUMN_USER_NAME, name);
            long erfolgreich = database.insert(AngelMemoDbHelper.TABLE_ANGEL_USER, null, values);

            if (erfolgreich != -1) {
                Log.d(LOG_TAG, "User erfolgreich hinzugefügt!");
            } else {
                Log.d(LOG_TAG, "User konnte nicht hinzugefügt werden!");
            }
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler addUser: " + e.getMessage());
        }
    }

    public void deleteUser(int id){

        try {
            int anzahl_deleted = database.delete(AngelMemoDbHelper.TABLE_ANGEL_USER, id + " = " + AngelMemoDbHelper.COLUMN_ID, null);

            Log.d(LOG_TAG, "Anzahl gelöschter User : " + anzahl_deleted + " !");
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler deleteUser: " + e.getMessage());
        }
    }

    public void deleteUserByName(String username){

        try {
            int anzahl_deleted = database.delete(AngelMemoDbHelper.TABLE_ANGEL_USER, AngelMemoDbHelper.COLUMN_USER_NAME+"=?", new String[] { username });

            Log.d(LOG_TAG, "Anzahl gelöschter User : " + anzahl_deleted + " !");
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler deleteUser: " + e.getMessage());
        }
    }

    public List<AngelMemoUser> getAllUser(){

        try {
            List<AngelMemoUser> angelMemoUserList = new ArrayList<>();

            Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_USER,
                    null, null, null, null, null, null);

            cursor.moveToFirst();
            AngelMemoUser angelMemoUser;

            while (!cursor.isAfterLast()) {
                angelMemoUser = cursorToAngelMemoUser(cursor);
                angelMemoUserList.add(angelMemoUser);
                cursor.moveToNext();
            }
            cursor.close();

            return angelMemoUserList;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler getAllUser: " + e.getMessage());
            return null;
        }
    }
    public List<String> getAllUsernames(){

        try {
            List<String> angelMemoUserList = new ArrayList<>();

            Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_USER,
                    null, null, null, null, null, null);

            cursor.moveToFirst();
            String angelMemoUsername;

            while (!cursor.isAfterLast()) {
                angelMemoUsername = cursorToAngelMemoUser(cursor).getUserName();
                angelMemoUserList.add(angelMemoUsername);
                cursor.moveToNext();
            }
            cursor.close();

            return angelMemoUserList;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler getAllUser: " + e.getMessage());
            return null;
        }
    }

    public AngelMemoUser getUserByName(String username){
        try {
            List<String> angelMemoUserList = new ArrayList<>();

            Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_USER,
                    null, AngelMemoDbHelper.COLUMN_USER_NAME+"=?", new String[] { username }, null, null, null);
            cursor.moveToFirst();
            AngelMemoUser angelMemoUser = null;

            while (!cursor.isAfterLast()) {
                angelMemoUser = (AngelMemoUser) cursorToAngelMemoUser(cursor);
                cursor.moveToNext();
            }
            cursor.close();
            return angelMemoUser;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler getAllUser: " + e.getMessage());
            return null;
        }
    }

    private AngelMemoUser cursorToAngelMemoUser(Cursor cursor) {
        try {
            int idIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_ID);
            int idName = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_USER_NAME);

            int id_name = cursor.getInt(idIndex);
            String name = cursor.getString(idName);

            AngelMemoUser angelMemoUser = new AngelMemoUser(id_name, name);

            return angelMemoUser;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler cursor User: " + e.getMessage());
            return null;
        }
    }

    public AngelMemoSensor getSensorById(int id){
        try {
            Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN, null, id + " = " + AngelMemoDbHelper.COLUMN_ID, null, null, null, null);
            cursor.moveToFirst();
            AngelMemoSensor angelMemoSensor;
            angelMemoSensor = cursorToAngelMemoSensor(cursor);
            cursor.close();

            return angelMemoSensor;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler getSensorById: " + e.getMessage());
            return null;
        }
    }
    public AngelMemoSensor getSensorByName(String name){

        try {
            Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN, null, "'"+name + "' = " + AngelMemoDbHelper.COLUMN_SENSOREN_NAME, null, null, null, null);
            cursor.moveToFirst();
            AngelMemoSensor angelMemoSensor;
            angelMemoSensor = cursorToAngelMemoSensor(cursor);
            cursor.close();

            return angelMemoSensor;
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Fehler getSensorByName: " + e.getMessage());
            return null;
        }
    }

    private AngelMemoSensor cursorToAngelMemoSensor(Cursor cursor){
        try {
            int idIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_ID);
            int idName = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_SENSOREN_NAME);

            int id_name = cursor.getInt(idIndex);
            String name = cursor.getString(idName);

            AngelMemoSensor angelMemoSensor = new AngelMemoSensor(id_name, name);

            return angelMemoSensor;
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Cursor Sensor: " + e.getMessage());
            return null;
        }
    }

    public void addWert(int userId, int sensorId, float wert){
        try{
            ContentValues values = new ContentValues();
            values.put(AngelMemoDbHelper.COLUMN_ID_USER, userId);
            values.put(AngelMemoDbHelper.COLUMN_ID_SENSOR, sensorId);
            values.put(AngelMemoDbHelper.COLUMN_VALUE, wert);

            long erfolgreich = database.insert(AngelMemoDbHelper.TABLE_ANGEL_WERTE, null, values);

            if (erfolgreich != -1) {
                Log.d(LOG_TAG, "Wert erfolgreich hinzugefügt!");
            } else {
                Log.d(LOG_TAG, "Wert konnte nicht hinzugefügt werden!");
            }
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler addWert: " + e.getMessage());
        }
    }
    public List<AngelMemoWerte> getWerte(int userId, AngelMemoSensor sensor){
        try {
            List<AngelMemoWerte> angelMemoWerteList = new ArrayList<>();

            //Cursor cursor = database.query(AngelMemoDbHelper.TABLE_ANGEL_WERTE,
                    //null, AngelMemoDbHelper.COLUMN_ID_USER + " = " + userId + " AND " + AngelMemoDbHelper.COLUMN_ID_SENSOR + " = " + sensor.getSensorId(), null, null, null, null);
            String sqlString = "";
            switch (SensorType.valueOf(sensor.getSensorName())){
                case HEARTRATE:
                    sqlString = "select *,avg(v."+AngelMemoDbHelper.COLUMN_VALUE+"), strftime('%Y-%m-%d',v."+AngelMemoDbHelper.COLUMN_DATE+") " +
                                "from "+AngelMemoDbHelper.TABLE_ANGEL_WERTE+" v " +
                                "where v."+AngelMemoDbHelper.COLUMN_ID_SENSOR+" = "+sensor.getSensorId()+" " +
                                "and v." + AngelMemoDbHelper.COLUMN_ID_USER + "=" + userId + " " +
                                "group by strftime('%Y-%m-%d',"+AngelMemoDbHelper.COLUMN_DATE+") order by v.date DESC";
                    break;
                case TEMPERATURE:
                    sqlString = "select *,avg(v."+AngelMemoDbHelper.COLUMN_VALUE+"), strftime('%Y-%m-%d',v."+AngelMemoDbHelper.COLUMN_DATE+") " +
                                "from "+AngelMemoDbHelper.TABLE_ANGEL_WERTE+" v " +
                                "where v."+AngelMemoDbHelper.COLUMN_ID_SENSOR+" = "+sensor.getSensorId()+" " +
                                "and v." + AngelMemoDbHelper.COLUMN_ID_USER + "=" + userId + " " +
                                "group by strftime('%Y-%m-%d',"+AngelMemoDbHelper.COLUMN_DATE+") order by v.date DESC";
                    break;
                case STEPCOUNTER:
                    sqlString = "select *,max(v."+AngelMemoDbHelper.COLUMN_VALUE+"), strftime('%Y-%m-%d',v."+AngelMemoDbHelper.COLUMN_DATE+") " +
                                "from "+AngelMemoDbHelper.TABLE_ANGEL_WERTE+" v " +
                                "where v."+AngelMemoDbHelper.COLUMN_ID_SENSOR+" = "+sensor.getSensorId()+" " +
                                "and v." + AngelMemoDbHelper.COLUMN_ID_USER + "=" + userId + " " +
                                "group by strftime('%Y-%m-%d',"+AngelMemoDbHelper.COLUMN_DATE+") order by v.date DESC";
                    break;
                case BATTERY:
                    sqlString = "select * from " + AngelMemoDbHelper.TABLE_ANGEL_WERTE + " " +
                                "v where "+ sensor.getSensorId() + "=v." + AngelMemoDbHelper.COLUMN_ID_SENSOR + " " +
                                "and v." + AngelMemoDbHelper.COLUMN_ID_USER + "=" + userId + " " +
                                "order by v.date DESC";
                    break;
            }

            Cursor cursor = database.rawQuery(sqlString,null);

            cursor.moveToFirst();
            AngelMemoWerte angelMemoWerte;

            while (!cursor.isAfterLast()) {
                angelMemoWerte = cursorToAngelMemoWerte(cursor);
                angelMemoWerteList.add(angelMemoWerte);
                cursor.moveToNext();
            }
            cursor.close();

            return angelMemoWerteList;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler getAllUser: " + e.getMessage());
            return null;
        }
    }
    private AngelMemoWerte cursorToAngelMemoWerte(Cursor cursor) {
        try {
            int idIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_ID);
            int idUserIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_ID_USER);
            int idSensorIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_ID_SENSOR);
            int idWertIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_VALUE);
            int idDatumIndex = cursor.getColumnIndex(AngelMemoDbHelper.COLUMN_DATE);

            int id_wert = cursor.getInt(idIndex);
            int id_user = cursor.getInt(idUserIndex);
            int id_sensor = cursor.getInt(idSensorIndex);
            String wert = cursor.getString(idWertIndex);
            String datum = cursor.getString(idDatumIndex);

            AngelMemoWerte angelMemoWerte = new AngelMemoWerte(id_wert, id_user, id_sensor, wert, datum);

            return angelMemoWerte;
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Fehler cursor Werte: " + e.getMessage());
            return null;
        }
    }

}