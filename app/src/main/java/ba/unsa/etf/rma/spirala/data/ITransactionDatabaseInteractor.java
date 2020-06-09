package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

public interface ITransactionDatabaseInteractor {
    void addTransaction(Context context, Transaction t);
    Transaction getTransaction(Context context, int internalId);
    void updateTransaction(Context context, Transaction t);
    void deleteTransaction(Context context, int internalId);
    ArrayList<Transaction> getTransactions(Context context);
    Cursor getTransactionCursor(Context context, Transaction.Type t, String orderBy, Date d);

}
