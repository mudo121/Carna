package com.example.christina.carna_ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Created by oguzbinbir on 07.06.16.
 */

public class AngelMemoDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = AngelMemoDbHelper.class.getSimpleName();

    public static final String DB_NAME = "AngelMemo.db";
    public static final int DB_VERSION = 1;

    //Table Werte
    public static final String TABLE_ANGEL_WERTE = "angelmemo_Werte";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_USER = "id_user";
    public static final String COLUMN_ID_SENSOR = "id_sensor";
    public static final String COLUMN_WERT = "Wert";
    public static final String COLUMN_DATUM = "Datum";


    //Table Sensoren
    public static final String TABLE_ANGEL_SENSOREN = "angelmemo_Sensoren";
    public static final String COLUMN_SENSOREN_NAME = "Name";

    //Table User
    public static final String TABLE_ANGEL_USER = "angelmemo_User";
    public static final String COLUMN_USER_NAME = "Name";


    //SQL Statemant creating Table Werte
    public static final String SQL_CREATE_TABLE_WERTE = "CREATE TABLE " + TABLE_ANGEL_WERTE +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ID_USER + " INTEGER NOT NULL REFERENCES " + TABLE_ANGEL_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE, " +
            COLUMN_ID_SENSOR + " INTEGER NOT NULL, " +
            COLUMN_WERT + " TEXT NOT NULL, " +
            COLUMN_DATUM + "  TIMESTAMP DEFAULT CURRENT_TIMESTAMP TEXT NOT NULL);";
    //Timestamp updated

    //SQL Statemant creating Table Sensoren
    public static final String SQL_CREATE_TABLE_SENSOREN = "CREATE TABLE " + TABLE_ANGEL_SENSOREN +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SENSOREN_NAME + " TEXT NOT NULL);";


    //SQL Statemant creating Table User
    public static final String SQL_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_ANGEL_USER +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_NAME + "PRIMARY KEY TEXT NOT NULL);";


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
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME,"Puls");
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME,"Temperatur");
            db.insert(AngelMemoDbHelper.TABLE_ANGEL_SENSOREN,null,values);
            values.put(AngelMemoDbHelper.COLUMN_SENSOREN_NAME,"Schritte");
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
