package com.example.slotr.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slotr.inventory.data.StockContract;
import com.example.slotr.inventory.data.StockDBHelper;


public class MainActivity extends AppCompatActivity implements  android.app.LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    StockCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddActivity.class);
                startActivity(i);

            }
        });


        getLoaderManager().initLoader(0,null,this);

        listView = (ListView) findViewById(R.id.list_view);
        cursorAdapter = new StockCursorAdapter(this,null);
        listView.setAdapter(cursorAdapter);
    }


    @Override
    public android.content.CursorLoader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), StockContract.StockEntry.CONTENT_URI,null,null,null,null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
        }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}

