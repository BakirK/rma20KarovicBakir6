package ba.unsa.etf.rma.spirala.list;

import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.data.Transaction;

public interface ITransactionListView {
    void setTransactions(ArrayList<Transaction> transactions);
    void notifyTransactionListDataSetChanged();
}
