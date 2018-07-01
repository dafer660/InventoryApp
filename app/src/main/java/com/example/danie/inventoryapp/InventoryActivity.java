package com.example.danie.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.danie.inventoryapp.data.InventoryCursorAdapter;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    private static final int INVENTORY_LOADER = 0;
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab_add_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, AddInventoryActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView itemsListView = findViewById(R.id.list_view_inventory);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemsListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        itemsListView.setAdapter(mCursorAdapter);

        // Setup OnItemClickListener
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent lvIntent = new Intent(InventoryActivity.this, AddInventoryActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                lvIntent.setData(currentItemUri);
                startActivity(lvIntent);
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER, null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyItem();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteDummyItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Perform SQL query
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QTY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(
                this,                   // Parent Activity context
                InventoryEntry.CONTENT_URI,     // Provider content URI
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection argument
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    // HELPER METHODS

    private void insertDummyItem() {

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Dummy Item");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 2);
        values.put(InventoryEntry.COLUMN_PRODUCT_QTY, 1);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Dummy Supplier");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "Dummy Phone");

        Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    private void deleteDummyItem() {
        String[] arguments = {"Dummy Item"};
        getContentResolver().delete(InventoryEntry.CONTENT_URI, InventoryEntry.COLUMN_PRODUCT_NAME + "=?", arguments);
    }

}
