package ba.unsa.etf.rma.spirala.detail;

import androidx.annotation.Nullable;

import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Callback;

public interface ITransactionDetailPresenter {
    void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                          @Nullable Integer transactionInterval, @Nullable Date endDate);

    void deleteTransaction();
    void setTransaction(Transaction t);
    Transaction getTransaction();
    void overMonthLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                        @Nullable Integer transactionInterval, @Nullable Date endDate);
    void overGlobalLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                         @Nullable Integer transactionInterval, @Nullable Date endDate);
}
