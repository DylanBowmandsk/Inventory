package com.example.slotr.inventory;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slotr.inventory.data.StockContract;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    TextView nameView;
    TextView quantityView;
    TextView priceView;
    TextView categoryView;
    ImageButton decrementView;
    ImageButton incrementView;
    ImageView imageView;
    int quantity;
    int id;
    Uri uri;
    Uri imageUri;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nameView = (TextView) findViewById(R.id.product_name_view);
        priceView = (TextView) findViewById(R.id.price_view);
        quantityView = (TextView) findViewById(R.id.quantity_view);
        categoryView = (TextView) findViewById(R.id.category_view);
        decrementView = (ImageButton) findViewById(R.id.quantity_decrement);
        incrementView = (ImageButton) findViewById(R.id.quantity_increment);
        imageView = (ImageView) findViewById(R.id.detail_image_view);

        //gets extras
        final int itemid = getIntent().getIntExtra("id", 0);

        //uri for current item
        uri = StockContract.StockEntry.CONTENT_URI.withAppendedPath(StockContract.StockEntry.CONTENT_URI, String.valueOf(itemid));

        //indexes for cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME);
        int categoryIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_CATEGORY);
        int priceIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE);
        int quantityIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY);
        int idIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_ID);
        int imageIndex = cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE);

        //initialise variables
        cursor.moveToNext();
        final String name = cursor.getString(nameIndex);
        final String category = cursor.getString(categoryIndex);
        final int price = cursor.getInt(priceIndex);
        quantity = cursor.getInt(quantityIndex);
        id = cursor.getInt(idIndex);
        imageUri = Uri.parse(cursor.getString(imageIndex));

        //fill  views
        nameView.setText(name);
        priceView.append(price + "");
        quantityView.setText(quantity + "");
        categoryView.setText(category);
        Log.v("tester", imageUri + "");
        addImage();


        //decrement quantity listener
        decrementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    quantity--;
                    quantityView.setText(quantity + "");
                    ContentValues values = new ContentValues();
                    values.put(StockContract.StockEntry.COLUMN_QUANTITY, quantity);
                    int affectedRows = getContentResolver().update(uri, values, null, null);
                }
            }
        });
        //increment quantity listener
        incrementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                quantityView.setText(quantity + "");
                ContentValues values = new ContentValues();
                values.put(StockContract.StockEntry.COLUMN_QUANTITY, quantity);
                int affectedRows = getContentResolver().update(uri, values, null, null);

            }
        });
    }


    //options menu setup
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            AlertDialog alert = createDialog();
            alert.setTitle("Delete");
            alert.show();
            return true;
        }
        if (id == R.id.order) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:"));
            i.putExtra(Intent.EXTRA_TEXT, "details here");
            startActivity(Intent.createChooser(i, "Fake Email"));
            return true;
        }
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEntry() {
        int index = getContentResolver().delete(StockContract.StockEntry.CONTENT_URI, StockContract.StockEntry.COLUMN_ID + "=?", new String[]{id + ""});
        if (index == -1) {
            Toast.makeText(getBaseContext(), "Could Not Delete", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getBaseContext(), "Deleted entry " + index, Toast.LENGTH_SHORT);
        }
    }

    public AlertDialog createDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
        dialog.setCancelable(true);
        dialog.setMessage("Are you sure");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteEntry();
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return dialog.create();
    }

    public void addImage() {
        if (ContextCompat.checkSelfPermission(DetailActivity.this,
                Manifest.permission.MANAGE_DOCUMENTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this,
                    new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    break;

                } else {

                }
                return;
            }
        }

    }
}
