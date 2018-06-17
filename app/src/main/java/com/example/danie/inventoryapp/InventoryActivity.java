package com.example.danie.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.danie.inventoryapp.data.InventoryDBHelper;

public class InventoryActivity extends AppCompatActivity {

    private InventoryDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyItem();
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDBHelper = new InventoryDBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertDummyItem();
                displayInventory();
                return true;

            case R.id.action_delete_all_entries:
                deleteDummyItem();
                displayInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayInventory();
    }

    // HELPER METHODS

    private void insertDummyItem() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Dummy Item");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 2);
        values.put(InventoryEntry.COLUMN_PRODUCT_QTY, 1);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Dummy Supplier");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "Dummy Phone");

        long idAdded = db.insert(InventoryEntry.TABLE_NAME,
                null,
                values);

        if (idAdded != -1) {
            Toast.makeText(this, "Item with ID " + idAdded + " was inserted into the DB " + InventoryEntry.TABLE_NAME, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Item with ID " + idAdded + " failed to be inserted into the DB " + InventoryEntry.TABLE_NAME, Toast.LENGTH_LONG).show();
        }

        displayInventory();
    }

    private void deleteDummyItem() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long idDeleted = db.delete(InventoryEntry.TABLE_NAME,
                null,
                null);

        if (idDeleted != -1) {
            Toast.makeText(this, "All items deleted from " + InventoryEntry.TABLE_NAME, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to delete all items from " + InventoryEntry.TABLE_NAME, Toast.LENGTH_LONG).show();
        }
    }

    private void displayInventory() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Perform SQL query
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QTY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        Cursor c = db.query(InventoryEntry.TABLE_NAME, projection,
                null, null,
                null, null, null);

        TextView displayView = (TextView) findViewById(R.id.inventory_text_view);

        try {

            displayView.setText("Inventory table contains " + c.getCount() + " items.\n\n");
            displayView.append(InventoryEntry._ID + " - " +
                    InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + " - " +
                    InventoryEntry.COLUMN_PRODUCT_QTY + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_NAME + " - " +
                    InventoryEntry.COLUMN_SUPPLIER_PHONE + "\n");

            int idColIdx = c.getColumnIndex(InventoryEntry._ID);
            int productNameColIdx = c.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int productPriceColIdx = c.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int productQtyColIdx = c.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QTY);
            int supplierNameColIdx = c.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColIdx = c.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            while (c.moveToNext()) {

                int currentProductID = c.getInt(idColIdx);
                String currentProductName = c.getString(productNameColIdx);
                int currentProductPrice = c.getInt(productPriceColIdx);
                int currentProductQty = c.getInt(productQtyColIdx);
                String currentSupplierName = c.getString(supplierNameColIdx);
                String currentSupplierPhone = c.getString(supplierPhoneColIdx);

                displayView.append(("\n" + currentProductID + " - " +
                        currentProductName + " - " +
                        currentProductPrice + " - " +
                        currentProductQty + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone + " - "));
            }

        } finally {
            // Always close the cursor when you're done reading from it.
            c.close();
        }
    }
}
