package ba.unsa.etf.rma.spirala;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public interface ITransactionListInteractor {
    ArrayList<Transaction> get();
    void addTransaction(Transaction t);
    void remove(int i);
    void set(int i, Transaction updatedTransaction);
    Transaction createTransaction(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                          @Nullable Integer transactionInterval, @Nullable Date endDate);
    Double getTotalAmount();
    Double getMonthlyAmount(Date date);
}

