package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Transaction;

public interface ITransactionListInteractor {
    Cursor getTransactionCursor(Context context, Transaction.Type t, String orderBy, Date d);
    void insertTransaction(Transaction t);
}

