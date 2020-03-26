package ba.unsa.etf.rma.spirala;

import java.util.ArrayList;

public interface ITransactionListInteractor {
    ArrayList<Transaction> get();
    void addTransaction(Transaction t);
    void remove(int i);

    void set(int i, Transaction updatedTransaction);
}
