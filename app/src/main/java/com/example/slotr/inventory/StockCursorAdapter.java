package com.example.slotr.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slotr.inventory.R;
import com.example.slotr.inventory.data.StockContract;
import com.example.slotr.inventory.data.StockContract.StockEntry;

/**
 * Created by SlotR on 11/07/2017.
 */

public class StockCursorAdapter extends CursorAdapter {

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name_view);
        TextView categoryView = (TextView) view.findViewById(R.id.category_view);
        TextView price_view = (TextView) view.findViewById(R.id.price_view);

        int nameIndex = cursor.getColumnIndex(StockEntry.COLUMN_NAME);
        int categoryIndex = cursor.getColumnIndex(StockEntry.COLUMN_CATEGORY);
        int priceIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);
        int idIndex = cursor.getColumnIndex(StockEntry.COLUMN_ID);

        final int id = cursor.getInt(idIndex);

        nameView.setText(cursor.getString(nameIndex));
        categoryView.setText(cursor.getString(categoryIndex));
        price_view.setText("£ " + cursor.getInt(priceIndex) + "");


        //current item id uri
        final Uri uri = StockContract.StockEntry.CONTENT_URI.withAppendedPath(StockContract.StockEntry.CONTENT_URI, String.valueOf(id));

        //set click event for sale of entry
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.sale_view);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int affecttedRows = 0;
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                int quantityindex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
                int quantity = cursor.getInt(quantityindex);
                ContentValues values = new ContentValues();
                values.put(StockEntry.COLUMN_QUANTITY, quantity - 1);
                affecttedRows = context.getContentResolver().update(uri, values, null, null);
                if (affecttedRows <= 0) {
                    Toast.makeText(context, "Value already 0", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Quantity = " + String.valueOf(quantity -1), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //starts new intent to details page for current cursor position
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("id", id);
                context.startActivity(i);
            }
        });
    }
}
