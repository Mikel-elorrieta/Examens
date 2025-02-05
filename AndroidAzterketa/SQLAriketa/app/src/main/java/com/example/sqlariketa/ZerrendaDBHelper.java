package com.example.sqlariketa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ZerrendaDBHelper extends SQLiteOpenHelper {

    // Datu-basearen informazioa
    private static final String DB_NAME = "ZerrendaDB.db";
    private static final int DB_VERSION = 2; // Cambiar de 1 a 2

    // Taularen eta zutabeen izenak
    public static final String TABLE_LENGOAIAK = "lengoaiak";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IZENA = "izena";
    public static final String COLUMN_DESKRIBAPENA = "deskribapena";
    public static final String COLUMN_SOFTWARELIBREA = "software_librea";
    //Integer batetara bihurtzeko, SQLite-ko ez dago boolean mota bat, O = false eta 1 = true

    // Taula sortzeko SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_LENGOAIAK + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IZENA + " TEXT NOT NULL, " +
                    COLUMN_DESKRIBAPENA + " TEXT NOT NULL, " +
                    COLUMN_SOFTWARELIBREA + " INTEGER NOT NULL DEFAULT 0);";

    public ZerrendaDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE); // Taula sortu
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LENGOAIAK); // Taula zaharra ezabatu
        onCreate(db); // Taula berria sortu
    }
}
