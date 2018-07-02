package com.example.danie.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.danie.inventoryapp.R;
import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private final Context mContext;
    private final Cursor mCursor;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
        this.mCursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate into list_item for fill out the text views
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView tvName = view.findViewById(R.id.name);
        TextView tvPrice = view.findViewById(R.id.price);
        TextView tvQuantity = view.findViewById(R.id.quantity);
        TextView tvSupplierName = view.findViewById(R.id.supplier_name);
        TextView tvSupplierPhone = view.findViewById(R.id.supplier_phone);

        final int columnId = cursor.getColumnIndexOrThrow(InventoryEntry._ID);
        final int columnItemName = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME);
        final int columnItemPrice = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE);
        final int columnItemQuantity = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QTY);
        final int columnSupplierName = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER_NAME);
        final int columnSupplierPhone = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_SUPPLIER_PHONE);

        final String itemName = cursor.getString(columnItemName);
        final String itemPrice = cursor.getString(columnItemPrice);
        String itemQuantity = cursor.getString(columnItemQuantity);
        String supplierName = cursor.getString(columnSupplierName);
        String supplierPhone = cursor.getString(columnSupplierPhone);

        if (TextUtils.isEmpty(itemQuantity)) {
            itemQuantity = context.getString(R.string.default_quantity);
        }

        // Update TextViews
        tvName.setText(itemName);
        tvPrice.setText(itemPrice);
        tvQuantity.setText(itemQuantity);
        tvSupplierName.setText(supplierName);
        tvSupplierPhone.setText(supplierPhone);

        // Define Sale button
        Button btnSale = view.findViewById(R.id.btn_sale);

        // Get the position of the cursor
        final int position = cursor.getPosition();

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // setup ContentValues
                ContentValues values = new ContentValues();

                // move to the position of the current operation
                cursor.moveToPosition(position);

                // Try to get the current quantity
                int oldQuantity = cursor.getInt(columnItemQuantity);
                int newQuantity = 0;
                if (oldQuantity > 0) {
                    newQuantity = oldQuantity - 1;
                }

                String supplierName = cursor.getString(columnSupplierName);
                String supplierPhone = cursor.getString(columnSupplierPhone);

                // Update the values
                values.put(InventoryEntry.COLUMN_PRODUCT_NAME, itemName);
                values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, itemPrice);
                values.put(InventoryEntry.COLUMN_PRODUCT_QTY, newQuantity);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

                String selection = InventoryEntry._ID + "=?";
                int itemId = cursor.getInt(columnId);
                String itemIdArg = Integer.toString(itemId);

                String[] selectionArgs = {itemIdArg};

                // Update the value
                int quantityUpdated = context.getContentResolver().update(
                        InventoryEntry.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                );

                Log.e(LOG_TAG, "row" + quantityUpdated);
                Log.e(LOG_TAG, "btn clicked");

            }
        });
    }
}
