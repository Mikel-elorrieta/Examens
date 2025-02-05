package com.example.sqlariketa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ZerrendaDAO {

    private final ZerrendaDBHelper dbHelper;

    // Klasearen konstruktorea: Datu-basearen laguntzailea sortu
    public ZerrendaDAO(Context context) {
        dbHelper = new ZerrendaDBHelper(context);
    }

    // CREATE: Lengoaia bat datu-basean gehitu
    public long gehituLengoaia(ProgramazioLengoaia lengoaia) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Idazteko moduan ireki datu-basea
        ContentValues values = new ContentValues();
        values.put(ZerrendaDBHelper.COLUMN_IZENA, lengoaia.getIzena());
        values.put(ZerrendaDBHelper.COLUMN_DESKRIBAPENA, lengoaia.getDeskribapena());
        values.put(ZerrendaDBHelper.COLUMN_SOFTWARELIBREA, lengoaia.isSoftwareLibrea() ? 1 : 0); // Booleano egokitu

        long id = db.insert(ZerrendaDBHelper.TABLE_LENGOAIAK, null, values);
        db.close();
        return id;
    }

    // READ: Lengoaia guztiak datu-basetik irakurri
    public List<ProgramazioLengoaia> lortuLengoaiak() {
        List<ProgramazioLengoaia> lengoaienZerrenda = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Irakurtzeko moduan ireki datu-basea

        // Lortu nahi diren zutabeak definitu
        String[] zutabeak = {
                ZerrendaDBHelper.COLUMN_ID,
                ZerrendaDBHelper.COLUMN_IZENA,
                ZerrendaDBHelper.COLUMN_DESKRIBAPENA,
                ZerrendaDBHelper.COLUMN_SOFTWARELIBREA
        };

        // Kontsulta egin taulari
        Cursor cursor = db.query(ZerrendaDBHelper.TABLE_LENGOAIAK, zutabeak,
                null, null, null, null, null);

        // Datu guztiak irakurri eta zerrendan gehitu
        if (cursor.moveToFirst()) {
            do {
                String izena = cursor.getString(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_IZENA));
                String deskribapena = cursor.getString(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_DESKRIBAPENA));
                boolean softwareLibrea = cursor.getInt(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_SOFTWARELIBREA)) > 0;
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_ID));
                lengoaienZerrenda.add(new ProgramazioLengoaia(id, izena, deskribapena, softwareLibrea));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lengoaienZerrenda;
    }

    // READ: Lengoaia zehatz bat ID bidez lortu
    public ProgramazioLengoaia lortuLengoaia(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Irakurtzeko moduan ireki datu-basea

        // Lortu nahi diren zutabeak definitu
        String[] zutabeak = {
                ZerrendaDBHelper.COLUMN_IZENA,
                ZerrendaDBHelper.COLUMN_DESKRIBAPENA,
                ZerrendaDBHelper.COLUMN_SOFTWARELIBREA
        };

        // Kontsulta egin ID zehatzari
        Cursor cursor = db.query(ZerrendaDBHelper.TABLE_LENGOAIAK, zutabeak,
                ZerrendaDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        // Datuak irakurri eta itzuli
        if (cursor != null && cursor.moveToFirst()) {
            String izena = cursor.getString(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_IZENA));
            String deskribapena = cursor.getString(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_DESKRIBAPENA));
            boolean softwareLibrea = cursor.getInt(cursor.getColumnIndexOrThrow(ZerrendaDBHelper.COLUMN_SOFTWARELIBREA)) > 0;
            cursor.close(); // Cursor itxi
            db.close(); // Datu-basea itxi
            return new ProgramazioLengoaia(izena, deskribapena, softwareLibrea);
        } else {
            db.close();
            return null; // Daturik ez badago, null itzuli
        }
    }

    // UPDATE: Lengoaia bat eguneratu ID bidez
    public int eguneratuLengoaia(int id, ProgramazioLengoaia lengoaia) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ZerrendaDBHelper.COLUMN_IZENA, lengoaia.getIzena());
        values.put(ZerrendaDBHelper.COLUMN_DESKRIBAPENA, lengoaia.getDeskribapena());
        values.put(ZerrendaDBHelper.COLUMN_SOFTWARELIBREA, lengoaia.isSoftwareLibrea() ? 1 : 0);

        // Eguneraketa egin ID zehatzari
        int eguneratutakoLerroKop = db.update(ZerrendaDBHelper.TABLE_LENGOAIAK, values,
                ZerrendaDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return eguneratutakoLerroKop;
    }

    // DELETE: Lengoaia bat ezabatu ID bidez
    public int ezabatuLengoaia(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ezabaketa egin ID zehatzari
        int ezabatuak = db.delete(ZerrendaDBHelper.TABLE_LENGOAIAK,
                ZerrendaDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return ezabatuak;
    }
}
