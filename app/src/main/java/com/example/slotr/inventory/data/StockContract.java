package com.example.slotr.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SlotR on 10/07/2017.
 */

public class StockContract {

    private StockContract() {
    }

    //content authority
    public static final String CONTENT_AUTHORITY = "com.example.slotr.stockapp";

    //path to database
    public static final String PATH_STOCK = "stock";

    //generic base URI for general use
    public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static class StockEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_URI.withAppendedPath(BASE_URI,PATH_STOCK);

        //table name
        public static final String TABLE_NAME = "stock";

        //table columns
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IMAGE = "image";

        //different categories for spinner
        public static final String CATEGORY_ELECTRICAL = "Electrical";
        public static final String CATEGORY_SPORT = "Sport";
        public static final String CATEGORY_DIY = "D.I.Y.";
        public static final String CATEGORY_UNKNOWN = "Unknown";

    }
}
