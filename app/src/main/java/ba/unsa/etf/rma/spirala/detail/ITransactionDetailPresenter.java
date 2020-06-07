package ba.unsa.etf.rma.spirala.detail;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.util.Callback;

public interface ITransactionDetailPresenter {
    void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                          @Nullable Integer transactionInterval, @Nullable Date endDate, boolean network);

    void deleteTransaction(boolean network);
    void setTransaction(Transaction t);
    Transaction getTransaction();
    void overMonthLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                        @Nullable Integer transactionInterval, @Nullable Date endDate);
    void overGlobalLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                         @Nullable Integer transactionInterval, @Nullable Date endDate);

    static Transaction getDatabaseTransaction(Context context, int internalId) {
        TransactionDatabaseInteractor transactionDatabaseInteractor = new TransactionDatabaseInteractor();
        return transactionDatabaseInteractor.getTransaction(context, internalId);
    }
}
