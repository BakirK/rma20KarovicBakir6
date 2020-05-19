package ba.unsa.etf.rma.spirala.list;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Transaction;

public interface ITransactionListInteractor {
    ArrayList<Transaction> getTransactions();
    void remove(int i);
    void set(int i, Transaction updatedTransaction);
    Transaction createTransaction(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                          @Nullable Integer transactionInterval, @Nullable Date endDate);
}

