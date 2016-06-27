package com.example.christina.carna_ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.christina.carna_ui.enumclass.SensorType;

/**
 * Created by oguzbinbir on 07.06.16.
 */

public class AngelMemoDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = AngelMemoDbHelper.class.getSimpleName();

    public static final String DB_NAME = "AngelMemo.db";
    public static final int DB_VERSION = 1;

    //Table Werte
    public static final String TABLE_ANGEL_WERTE = "sensor_readed_values";
    public static final String COLUMN_ID = "value_id";
    public static final String COLUMN_ID_USER = "value_user_id";
    public static final String COLUMN_ID_SENSOR = "value_sensor_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_DATE = "date";


    //Table Sensoren
    public static final String TABLE_ANGEL_SENSOREN = "sensors";
    public static final String COLUMN_SENSOREN_NAME = "sensor_name";

    //Table User
    public static final String TABLE_ANGEL_USER = "angelmemo_User";
    public static final String COLUMN_USER_NAME = "user_name";


    //SQL Statemant creating Table Werte
    public static final String SQL_CREATE_TABLE_WERTE = "CREATE TABLE " + TABLE_ANGEL_WERTE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ID_USER + " INTEGER NOT NULL REFERENCES " + TABLE_ANGEL_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE, " +
            COLUMN_ID_SENSOR + " INTEGER NOT NULL, " +
            COLUMN_VALUE + " REAL NOT NULL, " +
            COLUMN_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";
    //Timestamp updated

    //SQL Statemant creating Table Sensoren
    public static final String SQL_CREATE_TABLE_SENSOREN = "CREATE TABLE " + TABLE_ANGEL_SENSOREN +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SENSOREN_NAME + " TEXT UNIQUE NOT NULL);";


    //SQL Statemant creating Table User
    public static final String SQL_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_ANGEL_USER +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_NAME + " TEXT UNIQUE NOT NULL);";


    public AngelMemoDbHelper(Context context) {
        //Datenbank wird erzeugt - - - Context enthält Informationen über die Datenbankumgebung z.B. Pfad
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_TABLE_WERTE + " angelegt.");
            db.execSQL(SQL_CREATE_TABLE_WERTE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }

        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_TABLE_SENSOREN + " angelegt.");
            db.execSQL(SQL_CREATE_TABLE_SENSOREN);

            ContentValues values = new ContentValues();
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME, SensorType.HEARTRATE.toString());
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME, SensorType.TEMPERATURE.toString());
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME, SensorType.STEPCOUNTER.toString());
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME, SensorType.BATTERY.toString());
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);

        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }

        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_TABLE_USER + " angelegt.");
            db.execSQL(SQL_CREATE_TABLE_USER);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }

}
