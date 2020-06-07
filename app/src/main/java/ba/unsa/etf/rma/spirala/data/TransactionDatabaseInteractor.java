package ba.unsa.etf.rma.spirala.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

import ba.unsa.etf.rma.spirala.util.TransactionDBOpenHelper;

public class TransactionDatabaseInteractor implements ITransactionDatabaseInteractor {
    private Transaction transaction;

    public TransactionDatabaseInteractor(){}

    @Override
    public void addTransaction(Context context, Transaction t) {
        this.transaction = t;
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();
        Integer id = null;
        if(transaction.getId() != -1) {
            id = transaction.getId();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_ID, id);
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE, Transaction.format.format(transaction.getDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transaction.getType().toString());
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transaction.getTitle());

        Integer transactionInterval = null;
        if(transaction.getTransactionInterval() != null) {
            transactionInterval = transaction.getTransactionInterval();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL, transactionInterval);

        String endDate = null;
        if(transaction.getEndDate() != null) {
            endDate = Transaction.format.format(transaction.getEndDate());
        }

        values.put(TransactionDBOpenHelper.TRANSACTION_ENDDATE, endDate);
        String itemDescription = null;
        if(transaction.getItemDescription() != null) {
            itemDescription = transaction.getItemDescription();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION, itemDescription);
        Uri uri = cr.insert(address, values);
        Log.d("INSERT URI", uri.toString());
    }

    @Override
    public Transaction getTransaction(Context context, int internalId) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] columns = null;
        Uri address = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), internalId);
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cursor = cr.query(address, columns, where, whereArgs, order);
        if (cursor != null){
            cursor.moveToFirst();
            int id = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
            String date = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE));
            int amount = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
            int title = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
            int type = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
            int itemDescription = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION);
            int interval = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL);
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ENDDATE));

            Date tDate = null, eDate = null;
            if(date != null) {
                try {
                    tDate = Transaction.format.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(endDate != null) {
                try {
                    eDate = Transaction.format.parse(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            transaction = new Transaction(cursor.getInt(id), tDate,
                    cursor.getDouble(amount), cursor.getString(title), Transaction.Type.valueOf(cursor.getString(type)),
                    cursor.getString(itemDescription), cursor.getInt(interval) , eDate, internalId);
        }
        cursor.close();
        return transaction;
    }

    @Override
    public void updateTransaction(Context context, Transaction t) {
        this.transaction = t;
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        int internalId = t.getInternalId();
        Uri address = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), internalId);
        ContentValues values = new ContentValues();
        Integer id = null;
        if(transaction.getId() != -1) {
            id = transaction.getId();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_ID, id);
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE, Transaction.format.format(transaction.getDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transaction.getType().toString());
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transaction.getTitle());

        Integer transactionInterval = null;
        if(transaction.getTransactionInterval() != null) {
            transactionInterval = transaction.getTransactionInterval();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL, transactionInterval);

        String endDate = null;
        if(transaction.getEndDate() != null) {
            endDate = Transaction.format.format(transaction.getEndDate());
        }

        values.put(TransactionDBOpenHelper.TRANSACTION_ENDDATE, endDate);
        String itemDescription = null;
        if(transaction.getItemDescription() != null) {
            itemDescription = transaction.getItemDescription();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION, itemDescription);
        cr.update(address, values, null, null);
    }

    @Override
    public void deleteTransaction(Context context, int internalId) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), internalId);
        cr.delete(address, null, null);
    }
}
