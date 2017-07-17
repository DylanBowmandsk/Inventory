package com.example.slotr.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.slotr.inventory.data.StockContract.StockEntry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;

/**
 * Created by SlotR on 10/07/2017.
 */

public class StockProvider extends ContentProvider {
    StockDBHelper dbHelper;

    public static final int STOCK = 100;
    public static final int STOCK_ID = 101;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK, STOCK);
        uriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK + "/#", STOCK_ID);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new StockDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String s1) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        int match = uriMatcher.match(uri);
        switch (match) {
            //queries whole database
            case STOCK:
                cursor = db.query(StockEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            //queries database for single id
            case STOCK_ID:
                selection = StockContract.StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(StockContract.StockEntry.TABLE_NAME
                        , null
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , s1);
                break;
            default:
                throw new IllegalArgumentException("URI not accepted");

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long index = -1;
        int match = uriMatcher.match(uri);
        String name = contentValues.getAsString(StockEntry.COLUMN_NAME);
        int price = contentValues.getAsInteger(StockEntry.COLUMN_PRICE);
        int quantity = contentValues.getAsInteger(StockEntry.COLUMN_QUANTITY);
        String image = contentValues.getAsString(StockEntry.COLUMN_IMAGE);
        switch (match) {
            case STOCK:
                if (name != null & price != 0 & quantity != 0 & image != null) {
                    //checks if entry is duplicate
                    boolean isDuplicate = checkForDuplicate(uri, contentValues, db);
                    if (isDuplicate != true) {
                        index = db.insert(StockEntry.TABLE_NAME, null, contentValues);
                    } else {
                        index = -2;
                    }
                }

                break;
            default:
                throw new IllegalArgumentException("Check URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, index);
    }

    public boolean checkForDuplicate(Uri uri, ContentValues values, SQLiteDatabase db) {
        boolean isDuplicate = false;
        //sets variable for the name attempting to be entered
        String[] entry = new String[]{values.getAsString(StockContract.StockEntry.COLUMN_NAME)};
        //queries database for results with same name
        Cursor cursor = db.query(StockEntry.TABLE_NAME, null, StockEntry.COLUMN_NAME + "=?", entry, null, null, null);
        //if 1 or more entries are retrieved from databased duplicate is true
        if (cursor.getCount() > 0) {
            isDuplicate = true;
        }
        return isDuplicate;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int index = db.delete(StockEntry.TABLE_NAME, s, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return index;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int affectedRows = 0;
        switch (match) {
            case STOCK:
                throw new IllegalArgumentException("define entry to update");
            case STOCK_ID:
                int quantity = contentValues.getAsInteger(StockEntry.COLUMN_QUANTITY);
                if (quantity >= 0) {
                    selection = StockEntry.COLUMN_ID + "=?";
                    selectArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    affectedRows = db.update(StockEntry.TABLE_NAME, contentValues, selection, selectArgs);
                }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRows;
    }
}
