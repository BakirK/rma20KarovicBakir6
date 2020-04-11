package ba.unsa.etf.rma.spirala;

import ba.unsa.etf.rma.spirala.data.Transaction;

public interface OnItemClick {
    void displayTransaction(Transaction transaction);
    void updateTransactionListData();
    void displayAdded();
    void displayDeleted();
}

