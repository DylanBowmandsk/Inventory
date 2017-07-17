package com.example.slotr.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.slotr.inventory.data.StockContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.IllegalFormatException;

public class AddActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText priceEditText;
    EditText quantityEditText;
    Spinner categorySpinner;
    Button upload;
    ImageView image_view;
    String category;
    int IMAGE_REQUEST_CODE;
    Uri image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        IMAGE_REQUEST_CODE = 1;
        image = null;

        //initialise views
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        categorySpinner = (Spinner) findViewById(R.id.spinner_view);
        upload = (Button) findViewById(R.id.upload_button);
        image_view = (ImageView) findViewById(R.id.image_view);
        setupSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_button) {
            insertStock();
            return true;
        }
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method for inputting data to database
    private void insertStock() {
        String name = nameEditText.getText().toString().trim();
        if (name.length() == 0) {
            name = null;
        }
        String quantityString = quantityEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        int price = 0;
        int quantity = 0;
        try {
            price = Integer.parseInt(priceString);
            quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException ex) {
        }
        String uriString = null;
        if( image != null) {
            uriString = String.valueOf(image);
        }

        ContentValues values = new ContentValues();
        values.put(StockContract.StockEntry.COLUMN_NAME, name);
        values.put(StockContract.StockEntry.COLUMN_CATEGORY, category);
        values.put(StockContract.StockEntry.COLUMN_PRICE, price);
        values.put(StockContract.StockEntry.COLUMN_QUANTITY, quantity);
        values.put(StockContract.StockEntry.COLUMN_IMAGE,uriString);

        Uri returnedUri = getContentResolver().insert(StockContract.StockEntry.CONTENT_URI, values);
        int id = (int) ContentUris.parseId(returnedUri);
        if (id == -1) {
            Toast.makeText(getBaseContext(), "Could Not Insert Entry", Toast.LENGTH_SHORT).show();
        } else if (id == -2) {
            Toast.makeText(getBaseContext(), "Duplicate Entry", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Entry Created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            image = data.getData();
            Log.v("test",data.getData()+"");
            image_view.setImageURI(image);
            image_view.setBackground(null);
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {

        }
    }

    private void setupSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this
                , R.array.category_spinner_selection
                , R.layout.support_simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (selection.equals("D.I.Y.")) {
                    category = StockContract.StockEntry.CATEGORY_DIY;
                } else if (selection.equals("Electrical")) {
                    category = StockContract.StockEntry.CATEGORY_ELECTRICAL;
                } else if (selection.equals("Sport")) {
                    category = StockContract.StockEntry.CATEGORY_SPORT;
                } else {
                    category = StockContract.StockEntry.CATEGORY_UNKNOWN;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = StockContract.StockEntry.CATEGORY_UNKNOWN;
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, IMAGE_REQUEST_CODE);
            }
        });


    }
}

