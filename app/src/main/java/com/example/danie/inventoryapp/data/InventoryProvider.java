package com.example.danie.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.danie.inventoryapp.R;
import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the inventory table
     */
    private static final int ITEMS = 10;
    /**
     * URI matcher code for the content URI for a single item
     */
    private static final int ITEMS_ID = 11;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer for this class
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEMS_ID);
    }

    /**
     * InventoryDBHelper object
     */
    private InventoryDBHelper mDBHelper;

    /**
     * onCreate method that gets the context of the DB
     */
    @Override
    public boolean onCreate() {
        mDBHelper = new InventoryDBHelper(getContext());
        return false;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {

            case ITEMS:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEMS_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URL" + uri);
        }

        // If data changes, we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // returns the URI
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        // validate item name
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_name_provided));
        }

        // validate item price
        Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.negative_price));
        }

        // validate item quantity
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QTY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.negative_quantity));
        }

        // validate supplier name
        String supplier_name = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplier_name == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_supplier_name));
        }

        // validate supplier telephone
        String supplier_phone = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        if (supplier_phone == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_supplier_phone));
        }

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notifies all listeners with the ID of the newly appended at the end
        getContext().getContentResolver().notifyChange(uri, null);

        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        // create the variable
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ITEMS_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                // Get a generic error thrown if last cases are not met
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // this method returns the number of rows to be updated
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                // Update all rows that match the selection and selection args
                return updateInventory(uri, values, selection, selectionArgs);

            case ITEMS_ID:
                // Update a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);

            default:
                // Get a generic error thrown if last cases are not met
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // validate item name
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_name_provided));
        }

        // validate item price
        Double price = values.getAsDouble(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.negative_price));
        }

        // validate item quantity
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QTY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.negative_quantity));
        }

        // validate the supplier name
        String supplier_name = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);
        if (supplier_name == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_supplier_name));
        }

        // validate the supplier telephone
        String supplier_phone = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        if (supplier_phone == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.no_supplier_phone));
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows that were affected
        return rowsUpdated;
    }
}
