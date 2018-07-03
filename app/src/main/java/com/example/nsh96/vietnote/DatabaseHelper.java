package com.example.nsh96.vietnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final int VERSION = 5;

    public static final String CREATE_TABLE = "CREATE TABLE tblNote (\n" +
            VietNote.ID_NOTE + "        INTEGER       PRIMARY KEY AUTOINCREMENT,\n" +
            VietNote.TITLE + "         VARCHAR(128),\n" +
            VietNote.PASS + "        VARCHAR (128),\n" +
            VietNote.LOCKED + "          INTEGER (1),\n" +
            VietNote.DATE + "        VARCHAR (20),\n" +
            VietNote.TIME + "     VARCHAR (20),\n" +
            VietNote.CONTENT + "     VARCHAR (512),\n" +
            VietNote.IMAGE + "     VARCHAR (1024),\n" +
            VietNote.IMPORTANT + "         INTEGER (1),\n" +
            VietNote.RECORD + "     VARCHAR (1024)\n" +

            ");\n";

    public static final String CREATE_TABLE_PIN = "CREATE TABLE "+VietNote.TABLE_PIN+" (\n" +
            VietNote.PINCODE+"     INTEGER(8)\n" +
            ");\n";
    public static final String CREATE_TABLE_TIME = "CREATE TABLE "+VietNote.TABLE_TIME+" (\n" +
            VietNote._TIME+"     INTEGER(8),\n" +
            VietNote._UNIT+"     INTEGER(8)\n" +
            ");\n";

    public static final String CREATE_TABLE_SORT = "CREATE TABLE "+VietNote.TABLE_SORT+" (\n" +
            VietNote.UPDOWN+"     INTEGER(1),\n" +
            VietNote.STYLE_SORT+ "     INTEGER(1)\n" +
            ");\n";

    public DatabaseHelper(Context context) {
        super(context, VietNote.DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_PIN);
        db.execSQL(CREATE_TABLE_TIME);
        db.execSQL(CREATE_TABLE_SORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
