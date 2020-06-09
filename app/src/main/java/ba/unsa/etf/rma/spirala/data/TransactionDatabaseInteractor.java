package ba.unsa.etf.rma.spirala.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Override
    public ArrayList<Transaction> getTransactions(Context context) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] columns = null;
        Uri address = Uri.parse("content://rma.provider.transactions/elements");
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cursor = cr.query(address, columns, where, whereArgs, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
                String date = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE));
                int amount = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
                int title = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
                int type = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
                int itemDescription = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION);
                int interval = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL);
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ENDDATE));
                int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID);
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
                transactions.add(new Transaction(cursor.getInt(id), tDate,
                        cursor.getDouble(amount), cursor.getString(title), Transaction.Type.valueOf(cursor.getString(type)),
                        cursor.getString(itemDescription), cursor.getInt(interval) , eDate, cursor.getInt(internalId)));
            }
        }
        cursor.close();
        return transactions;
    }

    @Override
    public Cursor getTransactionCursor(Context context, Transaction.Type t, String orderBy, Date d) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL,
                TransactionDBOpenHelper.TRANSACTION_ENDDATE,
        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = "";
        ArrayList<String> whereArgs = new ArrayList<>();

        //type
        if(t != null && !t.toString().equals("ALL")) {
            where += "type = ? AND ";
            whereArgs.add(t.toString());
        }

        //date
        where += "date >= ? AND date <= ?";
        Calendar c = Transaction.toCalendar(d.getTime());
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));
        if(month.length() == 1) month = "0" + month;
        String firstDayDate = year + "-" + month + "-01";
        whereArgs.add(firstDayDate);

        //last day in month
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayDate = year + "-" + month + c.get(Calendar.DAY_OF_MONTH);
        whereArgs.add(lastDayDate);

        //order by clause
        String order = "";
        if(orderBy.startsWith("Price")) {
            order = "amount";
        } else if(orderBy.startsWith("Title")) {
            order = "title";
        } else {
            order = "date";
        }
        if(orderBy.endsWith("Ascending")) {
            order += " ASC";
        } else {
            order +=" DESC";
        }

        return cr.query(adresa, kolone, where, whereArgs.toArray(new String[whereArgs.size()]), order);
    }
}
