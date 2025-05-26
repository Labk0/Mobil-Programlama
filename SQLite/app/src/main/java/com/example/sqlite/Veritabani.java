package com.example.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Veritabani extends SQLiteOpenHelper {
    private static final String VERITAABANI_ADI = "Ogrenciler.db";
    private static final int VERITABANI_VERSION = 1;
    public Veritabani(Context context) {
        super(context, VERITAABANI_ADI, null, VERITABANI_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS OgrenciBilgi (" +
                "ad TEXT NOT NULL," +
                "soyad TEXT NOT NULL," +
                "yas INTEGER NOT NULL," +
                "sehir TEXT NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS OgrenciBilgi";
        db.execSQL(sql);
        onCreate(db);
    }
}
