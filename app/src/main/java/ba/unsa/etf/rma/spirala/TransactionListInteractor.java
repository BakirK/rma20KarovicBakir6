package ba.unsa.etf.rma.spirala;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

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

    @Override
    public void set(int i, Transaction updatedTransaction) {
        TransactionModel.transactions.set(i, updatedTransaction);
    }

    @Override
    public Transaction createTransaction(Date date,
                                         Double amount,
                                         String title,
                                         Transaction.Type type,
                                         @Nullable String itemDescription,
                                         @Nullable Integer transactionInterval,
                                         @Nullable Date endDate) {
        Transaction transaction = new Transaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
        TransactionModel.transactions.add(transaction);
        return  transaction;
    }
}
