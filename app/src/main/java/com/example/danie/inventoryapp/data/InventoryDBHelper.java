package com.example.danie.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Inventory.db";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
            + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
            + InventoryEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0,"
            + InventoryEntry.COLUMN_PRODUCT_QTY + " INTEGER NOT NULL DEFAULT 1,"
            + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT,"
            + InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
