package com.example.slotr.inventory.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SlotR on 10/07/2017.
 */

public class StockDBHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;

    private final static  String DATABASE_NAME = "stock.db";

    public StockDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_STOCK_TABLE = "CREATE TABLE " + StockContract.StockEntry.TABLE_NAME + " ("
                + StockContract.StockEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockContract.StockEntry.COLUMN_NAME +" TEXT NOT NULL, "
                + StockContract.StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + StockContract.StockEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + StockContract.StockEntry.COLUMN_CATEGORY + " TEXT NOT NULL, "
                +StockContract.StockEntry.COLUMN_IMAGE + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
