package com.example.danie.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danie.inventoryapp.data.InventoryContract.InventoryEntry;
import com.example.danie.inventoryapp.data.InventoryProvider;

public class AddInventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_ITEM_LOADER = 0;
    /**
     * EditText field to enter the item name
     */
    private EditText txtProductName;
    /**
     * TextView for the product price
     */
    private EditText txtProductPrice;
    /**
     * TextView for the product quantity
     */
    private TextView txtProductQuantity;
    /**
     * Button to increase quantity
     */
    private Button btnIncreaseQuantity;
    /**
     * Button to decrease quantity
     */
    private Button btnDecreaseQuantity;
    /**
     * EditText field to enter the supplier
     */
    private EditText txtSupplierName;
    /**
     * EditText field to enter the supplier phone
     */
    private EditText txtSupplierPhone;
    /**
     * Boolean flag that to check if item was updated or not (true or false, respectively)
     */
    private boolean mItemChanged = false;

    /**
     * Content URI for the existing item (null if it's new)
     */
    private Uri mCurrentUri = null;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.add_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // EditView and TextView
        txtProductName = findViewById(R.id.txt_product_name);
        txtProductPrice = findViewById(R.id.txt_product_price);
        txtProductQuantity = findViewById(R.id.txt_product_quantity);
        txtSupplierName = findViewById(R.id.txt_supplier_name);
        txtSupplierPhone = findViewById(R.id.txt_supplier_telephone);

        // Button View
        btnIncreaseQuantity = findViewById(R.id.plus_quantity);
        btnDecreaseQuantity = findViewById(R.id.minus_quantity);

        // Test if both Views are empty or null
        if (txtProductPrice.getText().toString().isEmpty() &&
                txtProductPrice.getText().toString().isEmpty()) {
            txtProductPrice.setText("0");
            txtProductQuantity.setText("1");
        }

        // OnClickListeners for the buttons that increase/decrease quantity
        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtQty = findViewById(R.id.txt_product_quantity);
                String currentQty = txtQty.getText().toString();
                int quantity = Integer.valueOf(currentQty) + 1;
                txtQty.setText(String.valueOf(quantity));
                Log.e(LOG_TAG, "currentQty" + currentQty);
                Log.e(LOG_TAG, "quantity" + String.valueOf(quantity));
            }
        });

        // OnTouchListeners
        txtProductName.setOnTouchListener(mTouchListener);
        txtProductPrice.setOnTouchListener(mTouchListener);
        txtProductQuantity.setOnTouchListener(mTouchListener);
        txtSupplierName.setOnTouchListener(mTouchListener);
        txtSupplierPhone.setOnTouchListener(mTouchListener);
        btnIncreaseQuantity.setOnTouchListener(mTouchListener);
        btnDecreaseQuantity.setOnTouchListener(mTouchListener);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QTY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE};

        return new CursorLoader(
                this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        if (data.moveToFirst()) {
            int itemNameColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int itemPriceColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int itemQuantityColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QTY);
            int supplierNameColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String itemName = data.getString(itemNameColumnIndex);
            double itemPrice = data.getDouble(itemPriceColumnIndex);
            int itemQuantity = data.getInt(itemQuantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierPhone = data.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            txtProductName.setText(itemName);
            txtProductPrice.setText(Double.toString(itemPrice));
            txtProductQuantity.setText(Integer.toString(itemQuantity));
            txtSupplierName.setText(supplierName);
            txtSupplierPhone.setText(supplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        txtProductName.setText("");
        txtProductPrice.setText("0");
        txtProductQuantity.setText("1");
        txtSupplierName.setText("");
        txtSupplierPhone.setText("");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mItemChanged) {
            // simply return if no update has been made
            super.onBackPressed();
            return;
        }

        // create the listener for the ui dialog
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

        // display dialog to the user
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option
            case R.id.action_insert_item:
                // Add a Pet to the DB
                addProduct();
                // Exit the activity and go back to MainActivity
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_item:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                if (!mItemChanged) {
                    NavUtils.navigateUpFromSameTask(AddInventoryActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddInventoryActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory_editor, menu);
        return true;
    }

    private void addProduct() {
        String itemName = txtProductName.getText().toString().trim();
        String itemPrice = txtProductPrice.getText().toString().trim();
        String itemQuantity = txtProductQuantity.getText().toString().trim();
        String supplierName = txtSupplierName.getText().toString().trim();
        String supplierPhone = txtSupplierPhone.getText().toString().trim();

        if (mCurrentUri == null &&
                TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, getString(R.string.add_item_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        int quantity = 0;

        if (!TextUtils.isEmpty(itemPrice)) {
            price = Double.parseDouble(itemPrice);
        }

        if (!TextUtils.isEmpty(itemQuantity)) {
            quantity = Integer.parseInt(itemQuantity);
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, itemName);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InventoryEntry.COLUMN_PRODUCT_QTY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        if (mCurrentUri == null) {

            Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (uri != null) {
                Toast.makeText(this, getString(R.string.add_item_sucess), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.add_item_failed), Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_item_success),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing item.
        if (mCurrentUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            int rowsDeleted = getContentResolver().delete(
                    mCurrentUri,
                    null,
                    null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(
                        this,
                        getString(R.string.delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(
                        this,
                        getString(R.string.delete_item_sucess),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(
                InventoryEntry.CONTENT_URI,
                null,
                null);

        Toast.makeText(
                this,
                rowsDeleted + " rows were deleted from " + InventoryEntry.TABLE_NAME,
                Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation);
        builder.setPositiveButton(R.string.delete_item, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.delete_item_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
