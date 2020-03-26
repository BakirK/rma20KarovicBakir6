package ba.unsa.etf.rma.spirala;

import java.util.ArrayList;

public class TransactionListInteractor implements ITransactionListInteractor {
    @Override
    public ArrayList<Transaction> get() {
        return TransactionModel.transactions;
    }

    @Override
    public void addTransaction(Transaction t) {
        TransactionModel.transactions.add(t);
    }

    @Override
    public void remove(int i) {
        TransactionModel.transactions.remove(i);
    }
}
