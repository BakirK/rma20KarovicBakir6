package ba.unsa.etf.rma.spirala;

import java.util.ArrayList;

public interface ITransactionListInteractor {
    ArrayList<Transaction> get();
    void addTransaction(Transaction t);
}
