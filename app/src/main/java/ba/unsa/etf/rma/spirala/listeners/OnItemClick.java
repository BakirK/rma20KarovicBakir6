package ba.unsa.etf.rma.spirala.listeners;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.Transaction;

public interface OnItemClick {
    void displayTransaction(Transaction transaction);
    void updateTransactionListData();
    void displayAdded();
    void displayDeleted();
    void displayAccount();
    void displayList();
    void displayGraphs();
}

