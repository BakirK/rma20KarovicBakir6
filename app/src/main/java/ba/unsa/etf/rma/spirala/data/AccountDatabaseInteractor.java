package ba.unsa.etf.rma.spirala.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import ba.unsa.etf.rma.spirala.util.TransactionDBOpenHelper;

public class AccountDatabaseInteractor implements IAccountDatabaseInteractor {
    public AccountDatabaseInteractor(){}

    @Override
    public void addAccount(Context context, Account account) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = Uri.parse("content://rma.provider.accounts/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.ACCOUNT_ID, account.getId());
        values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET, account.getBudget());
        values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT, account.getMonthLimit());
        values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, account.getTotalLimit());
        Uri uri = cr.insert(address, values);
        Log.d("INSERT URI", uri.toString());
    }

    @Override
    public Account getAccount(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] columns = null;
        Uri address = Uri.parse("content://rma.provider.accounts/elements");
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cursor = cr.query(address, columns, where, whereArgs, order);
        Account a = null;
        if (cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID));
            Double budget = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_BUDGET));
            Double monthLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT));
            Double globalLimit = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT));
            a = new Account(id, budget, globalLimit, monthLimit);
        }
        cursor.close();
        return  a;

    }

    @Override
    public void updateAccount(Context context, Account account) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = Uri.parse("content://rma.provider.accounts/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.ACCOUNT_ID, account.getId());
        values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET, account.getBudget());
        values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT, account.getMonthLimit());
        values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT, account.getTotalLimit());
        try {
            cr.update(address, values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = Uri.parse("content://rma.provider.accounts/elements");
        cr.delete(address, null, null);
    }
}
