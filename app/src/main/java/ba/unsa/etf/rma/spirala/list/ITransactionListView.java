package ba.unsa.etf.rma.spirala.list;

import android.database.Cursor;

import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.Transaction;

public interface ITransactionListView {
    void setTransactions(ArrayList<Transaction> transactions);
    void notifyTransactionListDataSetChanged();
    void setTextViewText(Account account);
    void setCursor(Cursor cursor);
}
